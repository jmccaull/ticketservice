package com.walmart.ticketservice.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import com.walmart.ticketservice.model.Venue;

/**
 * A Venue that manages issued seats and released seats. In this implementation,
 * lower seat numbers are better seats.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 *
 */
public final class LowestSeatPrefenceVenue implements Venue {

	private static final Logger LOG = LoggerFactory.getLogger(LowestSeatPrefenceVenue.class);

	private final int capacity;

	private final List<Integer> returnedSeats = new ArrayList<>();

	private int lastIssued;

	/**
	 * Starts issuing at seat number 1, up to and including capacity.
	 * 
	 * @param capacity must be > 0
	 * @throws IllegalArgumentException if capacity <= 0
	 */
	public LowestSeatPrefenceVenue(int capacity) {
		Preconditions.checkArgument(capacity > 0, String.format("capacity must be > 0, %s supplied", capacity));
		this.capacity = capacity;
		lastIssued = 0;
	}

	@Override
	public int[] getNSeats(int n) {
		Preconditions.checkArgument(n > 0, "n must be greater than 0");
		int[] seatsToReturn = null;
		synchronized (returnedSeats) {
			int remainingSeats = remainingSeats();
			int toGenerate = remainingSeats > n ? n : remainingSeats;
			LOG.debug("getNseats - n is {} remainingSeats is {}", n, remainingSeats);
			IntStream seatStream = Streams.concat(returnedSeats.stream().sorted().mapToInt(Integer::intValue),
					IntStream.generate(() -> ++lastIssued).limit(toGenerate - returnedSeats.size()));
			seatsToReturn = seatStream.toArray();
			LOG.debug("getNSeats returning {}", seatsToReturn);
		}
		return seatsToReturn;
	}

	@Override
	public void releaseSeats(int[] seatNumbers) {
		Preconditions.checkArgument(seatNumbers != null, "seatNumbers can not be null");
		synchronized (returnedSeats) {
			LOG.debug("Venue recieved back seats {}", seatNumbers);
			Arrays.stream(seatNumbers).forEach(this::verifyIssued);
			returnedSeats.addAll(Arrays.stream(seatNumbers).boxed().collect(Collectors.toList()));
			LOG.debug("All returned seats left: {}", returnedSeats);
		}
	}

	private void verifyIssued(int seat) {
		if (seat > lastIssued || returnedSeats.contains(seat)) {
			LOG.error("Seat {} was not issued or has been returned already", seat);
			throw new IllegalArgumentException(String.format("supplied seat %s never issued, can not return", seat));
		}
	}

	@Override
	public int remainingSeats() {
		return capacity + returnedSeats.size() - lastIssued;
	}

}

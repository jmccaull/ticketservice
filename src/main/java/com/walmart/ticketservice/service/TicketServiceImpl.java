package com.walmart.ticketservice.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.walmart.ticketservice.TicketService;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.Venue;
import com.walmart.ticketservice.model.impl.FailedHold;
import com.walmart.ticketservice.model.impl.SuccessfulHold;
import com.walmart.ticketservice.repo.SuccessfulHoldRepository;

/**
 * Manages the issuing and maintenance of reservations. Checks for expired
 * reservations only when necessary. "Best seat" selection, storage and hold
 * duration are configurable.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public final class TicketServiceImpl implements TicketService {

	private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

	private final Venue venue;

	private final Duration holdLimit;

	private final Random random = new Random();

	private final SuccessfulHoldRepository repo;

	/**
	 * Configure a ticket service with a Venue, hold time limit and storage. Will
	 * query repository for expired reservations when performing operations.
	 * Operations on the repository are synchronized. seatHoldIds are generated
	 * using the default random.
	 * 
	 * @param venue      can not be null.
	 * @param holdLimit  can not be null
	 * @param repository can not be null.
	 * @throws NullPointerException if any arg is null.
	 */
	public TicketServiceImpl(Venue venue, Duration holdLimit, SuccessfulHoldRepository repository) {
		this.venue = Preconditions.checkNotNull(venue);
		this.holdLimit = Preconditions.checkNotNull(holdLimit);
		this.repo = Preconditions.checkNotNull(repository);
	}

	@Override
	public int numSeatsAvailable() {
		int seats = 0;
		synchronized (repo) {
			removeExpiredReservations();
			seats = venue.remainingSeats();
		}
		LOG.debug("returning {} seats avaliable", seats);
		return seats;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		SeatHold reservation = null;
		if (numSeats <= 0) {
			LOG.error("numSeats {} < 0, returning failure", numSeats);
			reservation = new FailedHold("numSeats must be > 0");
		} else if (customerEmail == null) {
			LOG.error("customerEmail is null, returning failure");
			reservation = new FailedHold("customerEmail is null");
		} else {
			reservation = createReservation(numSeats, customerEmail);
		}
		return reservation;
	}

	private SeatHold createReservation(int numSeats, String customerEmail) {
		SeatHold reservation = null;
		synchronized (repo) {
			removeExpiredReservations();
			int remainingSeats = venue.remainingSeats();
			if (remainingSeats >= numSeats) {
				int[] seats = venue.getNSeats(numSeats);
				int seatHoldId = random.nextInt();
				Instant expirationTime = Instant.now().plus(holdLimit);
				reservation = new SuccessfulHold(expirationTime, seats, seatHoldId);
				repo.add(customerEmail, (SuccessfulHold) reservation);
				LOG.debug("hold created for {} for {} seats", customerEmail, numSeats);
			} else {
				LOG.error("Requested {} seats, only {} avalaible, returning failure", numSeats, remainingSeats);
				reservation = new FailedHold(String.format("Only %s seats avaliable", remainingSeats));
			}
		}
		return reservation;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		Preconditions.checkArgument(customerEmail != null, "customerEmail can not be null.");
		String confirmationCode = null;
		synchronized (repo) {
			SuccessfulHold selected = repo.getByEmail(customerEmail).stream()
					.filter(reservation -> seatHoldId == reservation.getSeatHoldId()).findFirst()
					.orElseThrow(() -> new IllegalArgumentException(String
							.format("Reservation for email: %s, id: %s does not exist", customerEmail, seatHoldId)));
			LOG.debug("found reservation for seatHoldId {}, email {}", seatHoldId, customerEmail);
			if (selected.isExpired()) {
				LOG.error("Reservation for seatHoldId {}, email {} is expired", seatHoldId, customerEmail);
				throw new IllegalStateException(
						String.format("Reservation for email: %s, id: %s is expired", customerEmail, seatHoldId));
			}
			confirmationCode = geneateConfirmationCode(customerEmail, selected);
			LOG.debug("reservation for seatHoldId {} email {} confirmed. Code: {}", seatHoldId, customerEmail,
					confirmationCode);
			repo.remove(customerEmail, selected);
		}
		return confirmationCode;
	}

	private String geneateConfirmationCode(String email, SuccessfulHold reservation) {
		return String.format("%s:%s:%s", email, reservation.getSeats(), Instant.now());
	}

	private void removeExpiredReservations() {
		Collection<Entry<String, SuccessfulHold>> expiredReservations = repo.getAllExpired();
		LOG.debug("Found {} expired reservations", expiredReservations.size());
		expiredReservations.forEach(entry -> {
			SuccessfulHold expired = entry.getValue();
			repo.remove(entry.getKey(), expired);
			venue.releaseSeats(expired.getSeats());
		});
	}
}

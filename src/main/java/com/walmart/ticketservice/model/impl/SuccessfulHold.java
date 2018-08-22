package com.walmart.ticketservice.model.impl;

import java.time.Instant;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.walmart.ticketservice.model.SeatHold;

/**
 * Represents a successful SeatHold. Has an expiration time, the seats reserved
 * along with a seatHoldId.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public final class SuccessfulHold implements SeatHold {

	private final Instant expiration;

	private final int[] seats;

	private final int holdId;

	/**
	 * Contains all information to confirm or undo reservation.
	 * 
	 * @param expiration can not be null
	 * @param seats      can not be null
	 * @param seatHoldId can be anything
	 * @throws NullPointerException if any arg is null
	 */
	public SuccessfulHold(Instant expiration, int[] seats, int seatHoldId) {
		this.expiration = Preconditions.checkNotNull(expiration);
		this.seats = Preconditions.checkNotNull(seats);
		this.holdId = seatHoldId;
	}

	/**
	 * @return if this reservation has expired according to the current system time.
	 */
	public boolean isExpired() {
		return Instant.now().isAfter(expiration);
	}

	/**
	 * @return a copy of the seats in this reservation.
	 */
	public int[] getSeats() {
		return Arrays.copyOf(seats, seats.length);
	}

	/**
	 * @return the id associated with this reservation.
	 */
	public int getSeatHoldId() {
		return holdId;
	}
}

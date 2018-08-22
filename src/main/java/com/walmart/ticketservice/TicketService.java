package com.walmart.ticketservice;

import com.walmart.ticketservice.model.SeatHold;

/**
 * TicketService interface provided as basis for project. Minor changes to
 * reflect expectations about underlying implementations.
 *
 */
public interface TicketService {
	/**
	 * The number of seats in the venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the venue
	 */
	int numSeatsAvailable();

	/**
	 * Find and hold the best available seats for a customer. Does not return null
	 * or throw exceptions for invalid input, instead returns a FailedHold.
	 *
	 * @param numSeats      the number of seats to find and hold
	 * @param customerEmail unique identifier for the customer
	 * @return either a SuccessfulHold identifying the specific seats and
	 *         related information, or a FailedHold with a description of the
	 *         failure.
	 */
	SeatHold findAndHoldSeats(int numSeats, String customerEmail);

	/**
	 * Commit seats held for a specific customer
	 *
	 * @param seatHoldId    the seat hold identifier. Must have been issued by this
	 *                      service.
	 * @param customerEmail the email address of the customer to which the seat hold
	 *                      is assigned. Can not be null.
	 * @throws IllegalArgumentException if email is null or the seatHoldId has not
	 *                                  been issued for the supplied email.
	 * @throws IllegalStateException    if the reservation has expired.
	 * @return a reservation confirmation code (format - email:seats:time)
	 */
	String reserveSeats(int seatHoldId, String customerEmail);
}

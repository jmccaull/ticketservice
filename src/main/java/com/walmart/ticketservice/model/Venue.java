package com.walmart.ticketservice.model;

/**
 * Manages issuing the current best seats, allowing previously issued seats to
 * be returned.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public interface Venue {

	/**
	 * Returns n sets, or however many remain - whichever is the lowest.
	 * 
	 * @param n must be > 0
	 * @throws IllegalArgumentException if seats <= 0
	 * @return possibly empty array containing up to requested number of seat ids.
	 */
	int[] getNSeats(int n);

	/**
	 * Returns seats back to the Venue. Seats must have been at least issued by this
	 * venue before.
	 * 
	 * @param seats can not be null
	 * @throws IllegalArgumentException if seats have not been issued or seats is
	 *                                  null.
	 */
	void releaseSeats(int[] seats);

	/**
	 * @return number of remaining seats.
	 */
	int remainingSeats();
}

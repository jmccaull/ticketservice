package com.walmart.ticketservice.repo;

import java.util.Collection;
import java.util.Map.Entry;

import com.walmart.ticketservice.model.impl.SuccessfulHold;

/**
 * A repository that provides required operations for storing/retrieving
 * reservations in a TicketService.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public interface SuccessfulHoldRepository {

	/**
	 * Obtain all reservations created by an email id.
	 * 
	 * @param email can not be null
	 * @throws IllegalArgumentException if email is null.
	 * @return possibly empty collection
	 */
	Collection<SuccessfulHold> getByEmail(String email);

	/**
	 * Obtains all expired reservations.
	 * 
	 * @return possibly empty collection.
	 */
	Collection<Entry<String, SuccessfulHold>> getAllExpired();

	/**
	 * Removes a reservation if it exists for the supplied email id.
	 * 
	 * @param email       can not be null
	 * @param reservation can not be null
	 * @throws IllegalArgumentException if either email or reservation is null.
	 */
	void remove(String email, SuccessfulHold reservation);

	/**
	 * Stores a reservation according to an email id.
	 * 
	 * @param email       can not be null
	 * @param reservation can not be null
	 * @throws IllegalArgumentException if either email or reservation are null.
	 */
	void add(String email, SuccessfulHold reservation);

}

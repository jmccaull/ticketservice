package com.walmart.ticketservice.repo.impl;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.walmart.ticketservice.model.impl.SuccessfulHold;
import com.walmart.ticketservice.repo.SuccessfulHoldRepository;

/**
 * A Mutlimap based repository that does not persist.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public final class HoldRepoNoPersistence implements SuccessfulHoldRepository {

	private static final Logger LOG = LoggerFactory.getLogger(HoldRepoNoPersistence.class);

	private final Multimap<String, SuccessfulHold> currentReservations = ArrayListMultimap.create();

	@Override
	public Collection<SuccessfulHold> getByEmail(String email) {
		Preconditions.checkArgument(email != null, "email can not be null");
		Collection<SuccessfulHold> reservations = currentReservations.get(email);
		LOG.debug("Found {} reservations for email {}", reservations.size(), email);
		return reservations;
	}

	@Override
	public void remove(String email, SuccessfulHold reservation) {
		Preconditions.checkArgument(email != null, "email can not be null");
		Preconditions.checkArgument(reservation != null, "reservation can not be null");
		LOG.debug("Removing reservation {} for email {}", reservation, email);
		currentReservations.remove(email, reservation);
	}

	@Override
	public void add(String email, SuccessfulHold reservation) {
		Preconditions.checkArgument(email != null, "email can not be null");
		Preconditions.checkArgument(reservation != null, "reservation can not be null");
		LOG.debug("adding reservation {} for email {}", reservation, email);
		currentReservations.put(email, reservation);
	}

	@Override
	public Collection<Entry<String, SuccessfulHold>> getAllExpired() {
		Collection<Entry<String, SuccessfulHold>> expired = currentReservations.entries().stream()
				.filter((entry) -> entry.getValue().isExpired()).collect(Collectors.toList());
		LOG.debug("Found {} expired reservations", expired.size());
		return expired;
	}

}

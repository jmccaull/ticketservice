package com.walmart.ticketservice.repo.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.walmart.ticketservice.model.impl.SuccessfulHold;
import com.walmart.ticketservice.repo.SuccessfulHoldRepository;

class NoPersistenceRepoTest {

	private static SuccessfulHold reservationOne;

	private static SuccessfulHold reservationTwo;

	private static SuccessfulHold reservationThree;

	private static final String EMAIL_ONE = "emailOne";

	private static final String EMAIL_TWO = "emailTwo";

	private SuccessfulHoldRepository repo;

	@BeforeAll
	static void createReservations() {
		Instant now = Instant.now();
		Duration day = Duration.ofDays(1);
		Random r = new Random();
		reservationOne = new SuccessfulHold(now, new int[] { 1, 2, 3 }, r.nextInt());
		reservationTwo = new SuccessfulHold(now.plus(day), new int[] { 1, 2, 3 }, r.nextInt());
		reservationThree = new SuccessfulHold(now.plus(day), new int[] { 1, 2, 3 }, r.nextInt());
	}

	@BeforeEach
	void createRepo() {
		repo = new HoldRepoNoPersistence();
		repo.add(EMAIL_ONE, reservationOne);
		repo.add(EMAIL_ONE, reservationTwo);
		repo.add(EMAIL_TWO, reservationThree);
	}

	@Test
	void shouldAddRemoveCorrectly() {
		repo.remove(EMAIL_ONE, reservationOne);
		Collection<SuccessfulHold> forEmailOne = repo.getByEmail(EMAIL_ONE);
		Collection<SuccessfulHold> forEmailTwo = repo.getByEmail(EMAIL_TWO);
		assertAll(() -> assertTrue(forEmailOne.contains(reservationTwo)),
				() -> assertFalse(forEmailOne.contains(reservationOne)),
				() -> assertTrue(forEmailTwo.contains(reservationThree)));
	}

	@Test
	void shouldFindOneExpired() {
		Collection<Entry<String, SuccessfulHold>> allExpired = repo.getAllExpired();
		assertTrue(allExpired.size() == 1);
		Entry<String, SuccessfulHold> expired = allExpired.stream().findFirst().orElse(null);
		assertAll(() -> expired.getKey().equals(EMAIL_ONE), () -> expired.getValue().equals(reservationOne));
	}

}

package com.walmart.ticketservice.model.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.walmart.ticketservice.model.Venue;

class LowestSeatVenueTest {

	private final static int VENUE_SIZE = 10;

	private Venue venue;

	@BeforeEach
	void initVenue() {
		venue = new LowestSeatPrefenceVenue(VENUE_SIZE);
	}

	@Test
	void reservesCorrectSeats() {
		int[] seats = venue.getNSeats(VENUE_SIZE);
		assertTrue(seats.length == VENUE_SIZE);
		assertTrue(venue.remainingSeats() == 0);
		venue.releaseSeats(seats);
		assertTrue(venue.remainingSeats() == VENUE_SIZE);
		seats = venue.getNSeats(10);
		assertTrue(venue.remainingSeats() == VENUE_SIZE);
	}

	@Test
	void shouldReturnCorrectNumSeats() {
		assertTrue(venue.remainingSeats() == VENUE_SIZE);
		int[] seats = venue.getNSeats(3);
		assertAll(() -> assertTrue(seats[0] == 1), () -> assertTrue(seats[1] == 2), () -> assertTrue(seats[2] == 3));
		venue.releaseSeats(new int[] { 2 });
		int[] moreSeats = venue.getNSeats(3);
		assertAll(() -> assertTrue(moreSeats[0] == 2), () -> assertTrue(moreSeats[1] == 4),
				() -> assertTrue(moreSeats[2] == 5));
	}

	@Test
	void shouldThrowExcpetion() {
		Throwable e = assertThrows(IllegalArgumentException.class, () -> venue.releaseSeats(new int[] { 3 }));
		assertEquals("supplied seat 3 never issued, can not return", e.getMessage());
	}

}

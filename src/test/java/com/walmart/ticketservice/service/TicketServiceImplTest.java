package com.walmart.ticketservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.walmart.ticketservice.TicketService;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.Venue;
import com.walmart.ticketservice.model.impl.FailedHold;
import com.walmart.ticketservice.model.impl.LowestSeatPrefenceVenue;
import com.walmart.ticketservice.model.impl.SuccessfulHold;
import com.walmart.ticketservice.repo.SuccessfulHoldRepository;
import com.walmart.ticketservice.repo.impl.HoldRepoNoPersistence;

class TicketServiceImplTest {

	private static final int VENUE_SIZE = 10;

	private static final String EMAIL_ONE = "emailOne";

	private static final String EMAIL_TWO = "emailTwo";

	private static TicketService ticketService;

	@BeforeAll
	static void initService() {
		Venue venue = new LowestSeatPrefenceVenue(VENUE_SIZE);
		Duration holdLimit = Duration.ofMinutes(1);
		SuccessfulHoldRepository repository = new HoldRepoNoPersistence();
		ticketService = new TicketServiceImpl(venue, holdLimit, repository);
	}

	@Test
	void shouldFailToReserve() {
		SeatHold hold = ticketService.findAndHoldSeats(VENUE_SIZE * 2, EMAIL_ONE);
		assertAll(() -> assertTrue(hold instanceof FailedHold),
				() -> assertEquals("Only 10 seats avaliable", ((FailedHold) hold).failureCode()));
		SeatHold holdTwo = ticketService.findAndHoldSeats(0, EMAIL_ONE);
		assertAll(() -> assertTrue(holdTwo instanceof FailedHold),
				() -> assertEquals("numSeats must be > 0", ((FailedHold) holdTwo).failureCode()));
		SeatHold holdThree = ticketService.findAndHoldSeats(VENUE_SIZE, null);
		assertAll(() -> assertTrue(holdThree instanceof FailedHold),
				() -> assertEquals("customerEmail is null", ((FailedHold) holdThree).failureCode()));
	}

	@Test
	void shouldThrowException() throws InterruptedException {
		Throwable e = assertThrows(IllegalArgumentException.class, () -> ticketService.reserveSeats(1, null));
		assertEquals("customerEmail can not be null.", e.getMessage());
		Throwable e2 = assertThrows(IllegalArgumentException.class, () -> ticketService.reserveSeats(1, EMAIL_ONE));
		assertEquals("Reservation for email: emailOne, id: 1 does not exist", e2.getMessage());
		Venue venue = new LowestSeatPrefenceVenue(VENUE_SIZE);
		SuccessfulHoldRepository repository = new HoldRepoNoPersistence();
		ticketService = new TicketServiceImpl(venue, Duration.ZERO, repository);
		SuccessfulHold reservation = (SuccessfulHold) ticketService.findAndHoldSeats(1, EMAIL_ONE);
		Throwable e3 = assertThrows(IllegalArgumentException.class,
				() -> ticketService.reserveSeats(reservation.getSeatHoldId(), EMAIL_TWO));
		String errorMessage = String.format("Reservation for email: %s, id: %s does not exist", EMAIL_TWO,
				reservation.getSeatHoldId());
		assertEquals(errorMessage, e3.getMessage());
		// Test runs too fast for this to eval correctly
		Thread.sleep(1);
		Throwable e4 = assertThrows(IllegalStateException.class,
				() -> ticketService.reserveSeats(reservation.getSeatHoldId(), EMAIL_ONE));
		String errorMessage2 = String.format("Reservation for email: %s, id: %s is expired", EMAIL_ONE,
				reservation.getSeatHoldId());
		assertEquals(errorMessage2, e4.getMessage());
		assertTrue(ticketService.numSeatsAvailable() == VENUE_SIZE);
	}

	@Test
	void shouldHoldAndReserve() {
		SuccessfulHold hold = (SuccessfulHold) ticketService.findAndHoldSeats(VENUE_SIZE, EMAIL_ONE);
		ticketService.reserveSeats(hold.getSeatHoldId(), EMAIL_ONE);
	}

}

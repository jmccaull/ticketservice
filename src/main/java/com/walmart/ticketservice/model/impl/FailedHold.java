package com.walmart.ticketservice.model.impl;

import com.google.common.base.Preconditions;
import com.walmart.ticketservice.model.SeatHold;

/**
 * Represents a failure to obtain a SeatHold. Contains a description about the
 * failure.
 * 
 * @author John McCaulley jmccaull@gmu.edu
 */
public final class FailedHold implements SeatHold {

	private final String reason;

	/**
	 * Essentially a null value object.
	 * 
	 * @param reason can not be null.
	 * @throws NullPointerException if reason is null.
	 */
	public FailedHold(String reason) {
		this.reason = Preconditions.checkNotNull(reason);
	}

	/**
	 * @return never null.
	 */
	public String failureCode() {
		return reason;
	}
}

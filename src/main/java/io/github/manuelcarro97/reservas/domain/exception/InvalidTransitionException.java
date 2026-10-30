package io.github.manuelcarro97.reservas.domain.exception;

import io.github.manuelcarro97.reservas.domain.model.ReservationStatus;

public class InvalidTransitionException extends DomainException {

	public InvalidTransitionException(ReservationStatus current) {
		super("INVALID_TRANSITION", "Action not allowed for a reservation in status " + current);
	}

	public InvalidTransitionException(ReservationStatus current, ReservationStatus target) {
		super("INVALID_TRANSITION", "Reservation cannot move from " + current + " to " + target + " yet");
	}

}

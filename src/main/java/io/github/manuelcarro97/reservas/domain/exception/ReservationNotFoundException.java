package io.github.manuelcarro97.reservas.domain.exception;

public class ReservationNotFoundException extends DomainException {

	public ReservationNotFoundException() {
		super("RESERVATION_NOT_FOUND", "Reservation not found");
	}

}

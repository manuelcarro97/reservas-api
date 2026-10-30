package io.github.manuelcarro97.reservas.domain.exception;

public class ClosureHasReservationsException extends DomainException {

	public ClosureHasReservationsException() {
		super("CLOSURE_HAS_RESERVATIONS", ErrorKind.CONFLICT, "Active reservations exist on that date");
	}

}

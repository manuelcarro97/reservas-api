package io.github.manuelcarro97.reservas.domain.exception;

public class ShiftNotOpenException extends DomainException {

	public ShiftNotOpenException() {
		super("SHIFT_NOT_OPEN", "The shift does not open on that date");
	}

}

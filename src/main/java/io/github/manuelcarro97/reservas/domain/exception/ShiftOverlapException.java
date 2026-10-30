package io.github.manuelcarro97.reservas.domain.exception;

public class ShiftOverlapException extends DomainException {

	public ShiftOverlapException() {
		super("SHIFT_OVERLAP", ErrorKind.CONFLICT, "The shift overlaps an existing shift");
	}

}

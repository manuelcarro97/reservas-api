package io.github.manuelcarro97.reservas.domain.exception;

public class ShiftNotFoundException extends DomainException {

	public ShiftNotFoundException() {
		super("SHIFT_NOT_FOUND", "Shift not found");
	}

}

package io.github.manuelcarro97.reservas.domain.exception;

public class DateInPastException extends DomainException {

	public DateInPastException() {
		super("DATE_IN_PAST", ErrorKind.UNPROCESSABLE, "The shift has already started");
	}

}

package io.github.manuelcarro97.reservas.domain.exception;

public class DateTooFarException extends DomainException {

	public DateTooFarException() {
		super("DATE_TOO_FAR", ErrorKind.UNPROCESSABLE, "The date is beyond the booking window");
	}

}

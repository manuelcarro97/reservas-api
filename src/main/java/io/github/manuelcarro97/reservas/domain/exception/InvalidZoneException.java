package io.github.manuelcarro97.reservas.domain.exception;

public class InvalidZoneException extends DomainException {

	public InvalidZoneException() {
		super("INVALID_REQUEST", "Unknown time zone");
	}

}

package io.github.manuelcarro97.reservas.domain.exception;

public class NoTableAvailableException extends DomainException {

	public NoTableAvailableException() {
		super("NO_TABLE_AVAILABLE", "No table available for that shift");
	}

}

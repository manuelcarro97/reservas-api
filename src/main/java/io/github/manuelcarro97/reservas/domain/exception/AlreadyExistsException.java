package io.github.manuelcarro97.reservas.domain.exception;

public class AlreadyExistsException extends DomainException {

	public AlreadyExistsException() {
		super("ALREADY_EXISTS", "The resource already exists");
	}

}

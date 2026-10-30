package io.github.manuelcarro97.reservas.domain.exception;

public class ConcurrentUpdateException extends DomainException {

	public ConcurrentUpdateException() {
		super("CONCURRENT_UPDATE", "The reservation was modified by another request");
	}

}

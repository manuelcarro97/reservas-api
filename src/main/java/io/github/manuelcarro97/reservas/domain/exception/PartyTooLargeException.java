package io.github.manuelcarro97.reservas.domain.exception;

public class PartyTooLargeException extends DomainException {

	public PartyTooLargeException() {
		super("PARTY_TOO_LARGE", "The party is larger than the biggest table");
	}

}

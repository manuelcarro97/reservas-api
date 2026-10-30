package io.github.manuelcarro97.reservas.domain.exception;

public class CancellationDeadlinePassedException extends DomainException {

	public CancellationDeadlinePassedException() {
		super("CANCELLATION_DEADLINE_PASSED", ErrorKind.CONFLICT, "The cancellation deadline has passed");
	}

}

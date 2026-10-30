package io.github.manuelcarro97.reservas.application.port.in;

public interface ExpirePendingReservationsUseCase {

	/** Cancela las reservas sin confirmar cuyo turno ya empezó y devuelve cuántas. */
	int expire();

}

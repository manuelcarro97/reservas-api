package io.github.manuelcarro97.reservas.application.port.in;

public interface CancelReservationUseCase {

	ReservationDetails cancelByCode(String code);

}

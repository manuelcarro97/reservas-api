package io.github.manuelcarro97.reservas.application.port.in;

public interface GetReservationUseCase {

	ReservationDetails byCode(String code);

}

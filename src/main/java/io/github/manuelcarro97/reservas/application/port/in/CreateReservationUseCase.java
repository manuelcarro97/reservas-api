package io.github.manuelcarro97.reservas.application.port.in;

import io.github.manuelcarro97.reservas.domain.model.Reservation;

public interface CreateReservationUseCase {

	Reservation create(String slug, CreateReservationCommand command);

}

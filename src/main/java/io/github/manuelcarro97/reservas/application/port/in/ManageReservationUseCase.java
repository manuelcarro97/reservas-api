package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalDate;
import java.util.List;

public interface ManageReservationUseCase {

	List<AdminReservation> listByDate(String slug, LocalDate date);

	ReservationDetails confirm(long id);

	ReservationDetails cancel(long id);

	ReservationDetails markNoShow(long id);

}

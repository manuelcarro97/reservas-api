package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalDate;
import java.util.List;

public interface GetAvailabilityUseCase {

	List<ShiftAvailability> availability(String slug, LocalDate date, int partySize);

}

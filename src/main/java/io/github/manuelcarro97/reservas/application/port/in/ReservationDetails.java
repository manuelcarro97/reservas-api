package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalDate;

import io.github.manuelcarro97.reservas.domain.model.ReservationStatus;

public record ReservationDetails(String code, ReservationStatus status, LocalDate date, long shiftId, int partySize,
		String tableLabel) {
}

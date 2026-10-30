package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalDate;

import io.github.manuelcarro97.reservas.domain.model.ReservationStatus;

public record AdminReservation(long id, String code, ReservationStatus status, LocalDate date, long shiftId,
		int partySize, String tableLabel, String customerName, String customerPhone, String customerEmail) {
}

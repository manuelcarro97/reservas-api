package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalDate;

public record CreateReservationCommand(LocalDate date, long shiftId, int partySize, String customerName,
		String customerPhone, String customerEmail) {
}

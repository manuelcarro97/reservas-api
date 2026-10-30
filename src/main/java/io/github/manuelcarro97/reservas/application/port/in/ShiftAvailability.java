package io.github.manuelcarro97.reservas.application.port.in;

import java.time.LocalTime;

public record ShiftAvailability(long shiftId, String name, LocalTime start, boolean available) {
}

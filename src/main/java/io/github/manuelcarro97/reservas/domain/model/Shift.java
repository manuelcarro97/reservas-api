package io.github.manuelcarro97.reservas.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

public record Shift(Long id, long restaurantId, String name, LocalTime start, LocalTime end, Set<DayOfWeek> days) {

	public boolean opensOn(LocalDate date) {
		return days.contains(date.getDayOfWeek());
	}

	public Instant startsAt(LocalDate date, ZoneId zone) {
		return date.atTime(start).atZone(zone).toInstant();
	}

}

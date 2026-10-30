package io.github.manuelcarro97.reservas.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ShiftTest {

	private static Shift shift(int startHour, int endHour, Set<DayOfWeek> days) {
		return new Shift(null, 1L, "Turno", LocalTime.of(startHour, 0), LocalTime.of(endHour, 0), days);
	}

	private static final Set<DayOfWeek> MONDAY = EnumSet.of(DayOfWeek.MONDAY);

	@Test
	void overlappingHoursOnSameDayOverlap() {
		assertThat(shift(13, 15, MONDAY).overlaps(shift(14, 16, MONDAY))).isTrue();
	}

	@Test
	void containedShiftOverlaps() {
		assertThat(shift(13, 18, MONDAY).overlaps(shift(14, 15, MONDAY))).isTrue();
	}

	@Test
	void adjacentShiftsDoNotOverlap() {
		assertThat(shift(13, 15, MONDAY).overlaps(shift(15, 17, MONDAY))).isFalse();
	}

	@Test
	void sameHoursOnDifferentDaysDoNotOverlap() {
		assertThat(shift(13, 15, MONDAY).overlaps(shift(13, 15, EnumSet.of(DayOfWeek.TUESDAY)))).isFalse();
	}

}

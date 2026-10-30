package io.github.manuelcarro97.reservas.domain.service;

import java.time.Clock;
import java.time.LocalDate;

import io.github.manuelcarro97.reservas.domain.exception.DateInPastException;
import io.github.manuelcarro97.reservas.domain.exception.DateTooFarException;
import io.github.manuelcarro97.reservas.domain.exception.PartyTooLargeException;
import io.github.manuelcarro97.reservas.domain.exception.RestaurantClosedException;
import io.github.manuelcarro97.reservas.domain.exception.ShiftNotOpenException;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;

public class BookingPolicy {

	public static final int BOOKING_WINDOW_DAYS = 90;

	public boolean isWithinWindow(Restaurant restaurant, LocalDate date, Clock clock) {
		LocalDate today = LocalDate.ofInstant(clock.instant(), restaurant.zone());
		return !date.isBefore(today) && !date.isAfter(today.plusDays(BOOKING_WINDOW_DAYS));
	}

	public void requireWithinWindow(Restaurant restaurant, LocalDate date, Clock clock) {
		LocalDate today = LocalDate.ofInstant(clock.instant(), restaurant.zone());
		if (date.isBefore(today)) {
			throw new DateInPastException();
		}
		if (date.isAfter(today.plusDays(BOOKING_WINDOW_DAYS))) {
			throw new DateTooFarException();
		}
	}

	public void validate(Restaurant restaurant, Shift shift, LocalDate date, int partySize, int maxCapacity,
			boolean closed, Clock clock) {
		requireWithinWindow(restaurant, date, clock);
		if (!shift.startsAt(date, restaurant.zone()).isAfter(clock.instant())) {
			throw new DateInPastException();
		}
		if (closed) {
			throw new RestaurantClosedException();
		}
		if (!shift.opensOn(date)) {
			throw new ShiftNotOpenException();
		}
		if (partySize > maxCapacity) {
			throw new PartyTooLargeException();
		}
	}

}

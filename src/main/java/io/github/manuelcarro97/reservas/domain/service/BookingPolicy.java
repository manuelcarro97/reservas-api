package io.github.manuelcarro97.reservas.domain.service;

import java.time.Clock;
import java.time.LocalDate;

import io.github.manuelcarro97.reservas.domain.exception.DateInPastException;
import io.github.manuelcarro97.reservas.domain.exception.PartyTooLargeException;
import io.github.manuelcarro97.reservas.domain.exception.RestaurantClosedException;
import io.github.manuelcarro97.reservas.domain.exception.ShiftNotOpenException;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;

public class BookingPolicy {

	public void validate(Restaurant restaurant, Shift shift, LocalDate date, int partySize, int maxCapacity,
			boolean closed, Clock clock) {
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

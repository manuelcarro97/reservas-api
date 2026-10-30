package io.github.manuelcarro97.reservas.application.service;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.manuelcarro97.reservas.application.port.in.ManageRestaurantUseCase;
import io.github.manuelcarro97.reservas.application.port.out.RestaurantRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.ShiftRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.TableRepositoryPort;
import io.github.manuelcarro97.reservas.domain.exception.InvalidZoneException;
import io.github.manuelcarro97.reservas.domain.exception.RestaurantNotFoundException;
import io.github.manuelcarro97.reservas.domain.model.DiningTable;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;

@Service
class RestaurantAdminService implements ManageRestaurantUseCase {

	private final RestaurantRepositoryPort restaurants;
	private final TableRepositoryPort tables;
	private final ShiftRepositoryPort shifts;

	RestaurantAdminService(RestaurantRepositoryPort restaurants, TableRepositoryPort tables,
			ShiftRepositoryPort shifts) {
		this.restaurants = restaurants;
		this.tables = tables;
		this.shifts = shifts;
	}

	@Override
	public Restaurant createRestaurant(String slug, String name, String zoneId, int cancellationDeadlineHours) {
		return restaurants.save(new Restaurant(null, slug, name, parseZone(zoneId), cancellationDeadlineHours));
	}

	@Override
	public DiningTable addTable(String slug, String label, int capacity, int posX, int posY) {
		return tables.save(new DiningTable(null, findRestaurant(slug).id(), label, capacity, posX, posY));
	}

	@Override
	public Shift addShift(String slug, String name, LocalTime start, LocalTime end, Set<DayOfWeek> days) {
		return shifts.save(new Shift(null, findRestaurant(slug).id(), name, start, end, days));
	}

	@Override
	public void addClosure(String slug, LocalDate date, String reason) {
		restaurants.addClosure(findRestaurant(slug).id(), date, reason);
	}

	private Restaurant findRestaurant(String slug) {
		return restaurants.findBySlug(slug).orElseThrow(RestaurantNotFoundException::new);
	}

	private static ZoneId parseZone(String zoneId) {
		try {
			return ZoneId.of(zoneId);
		}
		catch (DateTimeException unknownZone) {
			throw new InvalidZoneException();
		}
	}

}

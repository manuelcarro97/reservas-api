package io.github.manuelcarro97.reservas.application.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationUseCase;
import io.github.manuelcarro97.reservas.application.port.out.ReservationRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.RestaurantRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.ShiftRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.TableRepositoryPort;
import io.github.manuelcarro97.reservas.domain.exception.NoTableAvailableException;
import io.github.manuelcarro97.reservas.domain.exception.RestaurantNotFoundException;
import io.github.manuelcarro97.reservas.domain.exception.ShiftNotFoundException;
import io.github.manuelcarro97.reservas.domain.model.DiningTable;
import io.github.manuelcarro97.reservas.domain.model.Reservation;
import io.github.manuelcarro97.reservas.domain.model.ReservationCode;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;
import io.github.manuelcarro97.reservas.domain.service.BookingPolicy;
import io.github.manuelcarro97.reservas.domain.service.TableAssigner;

@Service
class ReservationService implements CreateReservationUseCase {

	private final RestaurantRepositoryPort restaurants;
	private final ShiftRepositoryPort shifts;
	private final TableRepositoryPort tables;
	private final ReservationRepositoryPort reservations;
	private final Clock clock;
	private final BookingPolicy bookingPolicy = new BookingPolicy();
	private final TableAssigner tableAssigner = new TableAssigner();

	ReservationService(RestaurantRepositoryPort restaurants, ShiftRepositoryPort shifts, TableRepositoryPort tables,
			ReservationRepositoryPort reservations, Clock clock) {
		this.restaurants = restaurants;
		this.shifts = shifts;
		this.tables = tables;
		this.reservations = reservations;
		this.clock = clock;
	}

	// Sin @Transactional: cada tryInsert confirma por sí mismo, y perder una mesa debe pasar a la siguiente.
	@Override
	public Reservation create(String slug, CreateReservationCommand command) {
		Restaurant restaurant = restaurants.findBySlug(slug).orElseThrow(RestaurantNotFoundException::new);
		Shift shift = shifts.findById(restaurant.id(), command.shiftId()).orElseThrow(ShiftNotFoundException::new);
		bookingPolicy.validate(restaurant, shift, command.date(), command.partySize(),
				restaurants.maxCapacity(restaurant.id()), restaurants.isClosed(restaurant.id(), command.date()), clock);

		List<DiningTable> free = tables.findFree(restaurant.id(), shift.id(), command.date());
		for (DiningTable table : tableAssigner.candidates(free, command.partySize())) {
			Optional<Reservation> inserted = reservations.tryInsert(pendingReservation(restaurant, table, command));
			if (inserted.isPresent()) {
				return inserted.get();
			}
		}
		throw new NoTableAvailableException();
	}

	private Reservation pendingReservation(Restaurant restaurant, DiningTable table, CreateReservationCommand command) {
		return Reservation.newPending(ReservationCode.generate(), restaurant.id(), command.shiftId(), table.id(),
				command.date(), command.partySize(), command.customerName(), command.customerPhone(),
				command.customerEmail(), clock.instant());
	}

}

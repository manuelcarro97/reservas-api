package io.github.manuelcarro97.reservas.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.manuelcarro97.reservas.application.port.in.AdminReservation;
import io.github.manuelcarro97.reservas.application.port.in.CancelReservationUseCase;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationUseCase;
import io.github.manuelcarro97.reservas.application.port.in.GetReservationUseCase;
import io.github.manuelcarro97.reservas.application.port.in.ManageReservationUseCase;
import io.github.manuelcarro97.reservas.application.port.in.ReservationDetails;
import io.github.manuelcarro97.reservas.application.port.out.ReservationRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.RestaurantRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.ShiftRepositoryPort;
import io.github.manuelcarro97.reservas.application.port.out.TableRepositoryPort;
import io.github.manuelcarro97.reservas.domain.exception.NoTableAvailableException;
import io.github.manuelcarro97.reservas.domain.exception.ReservationNotFoundException;
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
class ReservationService implements CreateReservationUseCase, GetReservationUseCase, CancelReservationUseCase,
		ManageReservationUseCase {

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

	@Override
	public ReservationDetails byCode(String code) {
		return details(findByCode(code));
	}

	@Override
	public ReservationDetails cancelByCode(String code) {
		Reservation reservation = findByCode(code);
		Restaurant restaurant = restaurants.findById(reservation.restaurantId())
			.orElseThrow(RestaurantNotFoundException::new);
		Shift shift = shifts.findById(restaurant.id(), reservation.shiftId())
			.orElseThrow(ShiftNotFoundException::new);
		reservation.cancelByCustomer(clock.instant(), shift.startsAt(reservation.date(), restaurant.zone()),
				restaurant.cancellationDeadlineHours());
		return details(reservations.update(reservation));
	}

	@Override
	public List<AdminReservation> listByDate(String slug, LocalDate date) {
		Restaurant restaurant = restaurants.findBySlug(slug).orElseThrow(RestaurantNotFoundException::new);
		return reservations.findByDate(restaurant.id(), date).stream().map(this::adminView).toList();
	}

	@Override
	public ReservationDetails confirm(long id) {
		Reservation reservation = findById(id);
		reservation.confirm();
		return details(reservations.update(reservation));
	}

	@Override
	public ReservationDetails cancel(long id) {
		Reservation reservation = findById(id);
		reservation.cancel();
		return details(reservations.update(reservation));
	}

	@Override
	public ReservationDetails markNoShow(long id) {
		Reservation reservation = findById(id);
		Restaurant restaurant = restaurants.findById(reservation.restaurantId())
			.orElseThrow(RestaurantNotFoundException::new);
		Shift shift = shifts.findById(restaurant.id(), reservation.shiftId())
			.orElseThrow(ShiftNotFoundException::new);
		reservation.markNoShow(clock.instant(), shift.startsAt(reservation.date(), restaurant.zone()));
		return details(reservations.update(reservation));
	}

	private Reservation findById(long id) {
		return reservations.findById(id).orElseThrow(ReservationNotFoundException::new);
	}

	private AdminReservation adminView(Reservation reservation) {
		return new AdminReservation(reservation.id(), reservation.code(), reservation.status(), reservation.date(),
				reservation.shiftId(), reservation.partySize(), tableLabel(reservation), reservation.customerName(),
				reservation.customerPhone(), reservation.customerEmail());
	}

	private String tableLabel(Reservation reservation) {
		return tables.findById(reservation.tableId()).map(DiningTable::label).orElseThrow();
	}

	private Reservation findByCode(String code) {
		return reservations.findByCode(code).orElseThrow(ReservationNotFoundException::new);
	}

	private ReservationDetails details(Reservation reservation) {
		return new ReservationDetails(reservation.code(), reservation.status(), reservation.date(),
				reservation.shiftId(), reservation.partySize(), tableLabel(reservation));
	}

	private Reservation pendingReservation(Restaurant restaurant, DiningTable table, CreateReservationCommand command) {
		return Reservation.newPending(ReservationCode.generate(), restaurant.id(), command.shiftId(), table.id(),
				command.date(), command.partySize(), command.customerName(), command.customerPhone(),
				command.customerEmail(), clock.instant());
	}

}

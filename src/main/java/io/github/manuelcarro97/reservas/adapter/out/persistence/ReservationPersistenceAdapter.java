package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import io.github.manuelcarro97.reservas.application.port.out.ReservationRepositoryPort;
import io.github.manuelcarro97.reservas.domain.exception.ConcurrentUpdateException;
import io.github.manuelcarro97.reservas.domain.model.Reservation;

@Component
class ReservationPersistenceAdapter implements ReservationRepositoryPort {

	// Va por JDBC porque JPA no expresa ON CONFLICT, y un conflicto no debe abortar la transacción.
	private static final String INSERT_IF_SLOT_FREE = """
			INSERT INTO reservation (code, restaurant_id, shift_id, table_id, reservation_date, party_size,
			                         customer_name, customer_phone, customer_email, status, version, created_at)
			VALUES (:code, :restaurantId, :shiftId, :tableId, :date, :partySize,
			        :customerName, :customerPhone, :customerEmail, :status, 0, :createdAt)
			ON CONFLICT (table_id, reservation_date, shift_id) WHERE status IN ('PENDING', 'CONFIRMED') DO NOTHING
			RETURNING id
			""";

	private final ReservationJpaRepository reservations;
	private final JdbcClient jdbc;

	ReservationPersistenceAdapter(ReservationJpaRepository reservations, JdbcClient jdbc) {
		this.reservations = reservations;
		this.jdbc = jdbc;
	}

	@Override
	public Optional<Reservation> tryInsert(Reservation reservation) {
		return jdbc.sql(INSERT_IF_SLOT_FREE)
			.param("code", reservation.code())
			.param("restaurantId", reservation.restaurantId())
			.param("shiftId", reservation.shiftId())
			.param("tableId", reservation.tableId())
			.param("date", reservation.date())
			.param("partySize", reservation.partySize())
			.param("customerName", reservation.customerName())
			.param("customerPhone", reservation.customerPhone())
			.param("customerEmail", reservation.customerEmail())
			.param("status", reservation.status().name())
			.param("createdAt", Timestamp.from(reservation.createdAt()))
			.query(Long.class)
			.optional()
			.map(id -> new Reservation(id, reservation.code(), reservation.restaurantId(), reservation.shiftId(),
					reservation.tableId(), reservation.date(), reservation.partySize(), reservation.customerName(),
					reservation.customerPhone(), reservation.customerEmail(), reservation.status(), 0,
					reservation.createdAt()));
	}

	@Override
	public Optional<Reservation> findByCode(String code) {
		return reservations.findByCode(code).map(ReservationEntity::toDomain);
	}

	@Override
	public Optional<Reservation> findById(long id) {
		return reservations.findById(id).map(ReservationEntity::toDomain);
	}

	@Override
	public Reservation update(Reservation reservation) {
		try {
			return reservations.save(ReservationEntity.from(reservation)).toDomain();
		}
		catch (OptimisticLockingFailureException staleVersion) {
			throw new ConcurrentUpdateException();
		}
	}

	@Override
	public List<Reservation> findByDate(long restaurantId, LocalDate date) {
		return reservations.findByRestaurantIdAndDateOrderById(restaurantId, date)
			.stream()
			.map(ReservationEntity::toDomain)
			.toList();
	}

	@Override
	public Set<Long> occupiedTableIds(long restaurantId, long shiftId, LocalDate date) {
		return Set.copyOf(reservations.occupiedTableIds(restaurantId, shiftId, date));
	}

}

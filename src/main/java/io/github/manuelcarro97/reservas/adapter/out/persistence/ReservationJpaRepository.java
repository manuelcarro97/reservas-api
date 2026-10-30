package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

	Optional<ReservationEntity> findByCode(String code);

	List<ReservationEntity> findByRestaurantIdAndDateOrderById(long restaurantId, LocalDate date);

	@Query("""
			select r.tableId from ReservationEntity r
			where r.restaurantId = :restaurantId and r.shiftId = :shiftId and r.date = :date
			  and r.status in (io.github.manuelcarro97.reservas.domain.model.ReservationStatus.PENDING,
			                   io.github.manuelcarro97.reservas.domain.model.ReservationStatus.CONFIRMED)
			""")
	List<Long> occupiedTableIds(long restaurantId, long shiftId, LocalDate date);

}

package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

interface ClosureJpaRepository extends JpaRepository<ClosureEntity, Long> {

	boolean existsByRestaurantIdAndDate(long restaurantId, LocalDate date);

}

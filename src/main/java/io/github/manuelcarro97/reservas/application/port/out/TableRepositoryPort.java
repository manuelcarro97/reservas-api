package io.github.manuelcarro97.reservas.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.manuelcarro97.reservas.domain.model.DiningTable;

public interface TableRepositoryPort {

	List<DiningTable> findFree(long restaurantId, long shiftId, LocalDate date);

	List<DiningTable> findAll(long restaurantId);

	Optional<DiningTable> findById(long id);

	DiningTable save(DiningTable table);

}

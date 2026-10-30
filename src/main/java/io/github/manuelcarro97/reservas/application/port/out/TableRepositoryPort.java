package io.github.manuelcarro97.reservas.application.port.out;

import java.time.LocalDate;
import java.util.List;

import io.github.manuelcarro97.reservas.domain.model.DiningTable;

public interface TableRepositoryPort {

	List<DiningTable> findFree(long restaurantId, long shiftId, LocalDate date);

	List<DiningTable> findAll(long restaurantId);

	DiningTable save(DiningTable table);

}

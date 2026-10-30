package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import io.github.manuelcarro97.reservas.application.port.out.TableRepositoryPort;
import io.github.manuelcarro97.reservas.domain.model.DiningTable;

@Component
class TablePersistenceAdapter implements TableRepositoryPort {

	private final DiningTableJpaRepository tables;

	TablePersistenceAdapter(DiningTableJpaRepository tables) {
		this.tables = tables;
	}

	@Override
	public List<DiningTable> findFree(long restaurantId, long shiftId, LocalDate date) {
		return tables.findFree(restaurantId, shiftId, date).stream().map(DiningTableEntity::toDomain).toList();
	}

	@Override
	public List<DiningTable> findAll(long restaurantId) {
		return tables.findByRestaurantIdOrderById(restaurantId).stream().map(DiningTableEntity::toDomain).toList();
	}

	@Override
	public DiningTable save(DiningTable table) {
		return tables.save(DiningTableEntity.from(table)).toDomain();
	}

}

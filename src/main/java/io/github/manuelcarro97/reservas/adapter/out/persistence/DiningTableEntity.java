package io.github.manuelcarro97.reservas.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.github.manuelcarro97.reservas.domain.model.DiningTable;

@Entity
@Table(name = "dining_table")
class DiningTableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "restaurant_id")
	private long restaurantId;

	private String label;

	private int capacity;

	@Column(name = "pos_x")
	private int posX;

	@Column(name = "pos_y")
	private int posY;

	protected DiningTableEntity() {
	}

	static DiningTableEntity from(DiningTable table) {
		DiningTableEntity entity = new DiningTableEntity();
		entity.id = table.id();
		entity.restaurantId = table.restaurantId();
		entity.label = table.label();
		entity.capacity = table.capacity();
		entity.posX = table.posX();
		entity.posY = table.posY();
		return entity;
	}

	DiningTable toDomain() {
		return new DiningTable(id, restaurantId, label, capacity, posX, posY);
	}

}

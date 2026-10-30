package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "closure")
class ClosureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "restaurant_id")
	private long restaurantId;

	private LocalDate date;

	private String reason;

	protected ClosureEntity() {
	}

	ClosureEntity(long restaurantId, LocalDate date, String reason) {
		this.restaurantId = restaurantId;
		this.date = date;
		this.reason = reason;
	}

}

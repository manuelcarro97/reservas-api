package io.github.manuelcarro97.reservas.adapter.out.persistence;

import java.time.ZoneId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.github.manuelcarro97.reservas.domain.model.Restaurant;

@Entity
@Table(name = "restaurant")
class RestaurantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String slug;

	private String name;

	@Column(name = "zone_id")
	private String zoneId;

	@Column(name = "cancellation_deadline_hours")
	private int cancellationDeadlineHours;

	protected RestaurantEntity() {
	}

	static RestaurantEntity from(Restaurant restaurant) {
		RestaurantEntity entity = new RestaurantEntity();
		entity.id = restaurant.id();
		entity.slug = restaurant.slug();
		entity.name = restaurant.name();
		entity.zoneId = restaurant.zone().getId();
		entity.cancellationDeadlineHours = restaurant.cancellationDeadlineHours();
		return entity;
	}

	Restaurant toDomain() {
		return new Restaurant(id, slug, name, ZoneId.of(zoneId), cancellationDeadlineHours);
	}

}

CREATE TABLE restaurant (
    id                          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    slug                        VARCHAR(80)  NOT NULL UNIQUE,
    name                        VARCHAR(120) NOT NULL,
    zone_id                     VARCHAR(60)  NOT NULL,
    cancellation_deadline_hours INTEGER      NOT NULL DEFAULT 2 CHECK (cancellation_deadline_hours >= 0)
);

CREATE TABLE dining_table (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT      NOT NULL REFERENCES restaurant (id),
    label         VARCHAR(40) NOT NULL,
    capacity      INTEGER     NOT NULL CHECK (capacity > 0),
    pos_x         INTEGER     NOT NULL,
    pos_y         INTEGER     NOT NULL,
    UNIQUE (restaurant_id, label)
);

CREATE TABLE shift (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT      NOT NULL REFERENCES restaurant (id),
    name          VARCHAR(60) NOT NULL,
    start_time    TIME        NOT NULL,
    end_time      TIME        NOT NULL,
    days_of_week  SMALLINT[]  NOT NULL,
    CHECK (start_time < end_time)
);

CREATE TABLE closure (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurant (id),
    date          DATE   NOT NULL,
    reason        VARCHAR(200),
    UNIQUE (restaurant_id, date)
);

CREATE TABLE reservation (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code             VARCHAR(16)  NOT NULL UNIQUE,
    restaurant_id    BIGINT       NOT NULL REFERENCES restaurant (id),
    shift_id         BIGINT       NOT NULL REFERENCES shift (id),
    table_id         BIGINT       NOT NULL REFERENCES dining_table (id),
    reservation_date DATE         NOT NULL,
    party_size       INTEGER      NOT NULL CHECK (party_size > 0),
    customer_name    VARCHAR(100) NOT NULL,
    customer_phone   VARCHAR(30)  NOT NULL,
    customer_email   VARCHAR(254),
    status           VARCHAR(20)  NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'NO_SHOW')),
    version          BIGINT       NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservation_restaurant_date ON reservation (restaurant_id, reservation_date);

-- Garantía anti-overbooking: una mesa solo admite una reserva activa por fecha y turno.
CREATE UNIQUE INDEX uq_reservation_active_slot
    ON reservation (table_id, reservation_date, shift_id)
    WHERE status IN ('PENDING', 'CONFIRMED');

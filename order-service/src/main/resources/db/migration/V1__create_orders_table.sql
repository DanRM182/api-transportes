CREATE TABLE orders (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_orders_status
        CHECK (status IN (
            'CREATED',
            'IN_TRANSIT',
            'DELIVERED',
            'CANCELLED')),

    CONSTRAINT chk_orders_origin_not_blank
        CHECK (TRIM(origin) <> ''),

    CONSTRAINT chk_orders_destination_not_blank
        CHECK (TRIM(destination) <> '')
);
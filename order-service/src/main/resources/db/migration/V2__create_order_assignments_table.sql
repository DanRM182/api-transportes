CREATE TABLE order_assignments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    pdf_path VARCHAR(255),
    image_path VARCHAR(255),

    CONSTRAINT fk_order_assignments_order_id
        FOREIGN KEY (order_id)
        REFERENCES orders(id),

    CONSTRAINT uq_order_assignments_order_id
        UNIQUE (order_id)
);
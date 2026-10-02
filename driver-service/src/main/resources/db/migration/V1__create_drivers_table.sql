CREATE TABLE drivers (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    license_number VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_drivers_name_not_blank
        CHECK (TRIM(name) <> ''),

    CONSTRAINT uq_drivers_license_number
        UNIQUE (license_number),

    CONSTRAINT chk_drivers_license_number_not_blank
        CHECK (TRIM(license_number) <> '')
)
CREATE TABLE inventory (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id          UUID NOT NULL UNIQUE,
    product_name        VARCHAR(255) NOT NULL,
    quantity_available  INTEGER NOT NULL DEFAULT 0,
    quantity_reserved   INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_available_non_negative CHECK (quantity_available >= 0),
    CONSTRAINT chk_reserved_non_negative  CHECK (quantity_reserved >= 0)
);
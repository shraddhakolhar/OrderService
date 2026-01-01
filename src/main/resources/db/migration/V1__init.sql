CREATE TABLE orders
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    total_amount DOUBLE NOT NULL,
    status ENUM(
        'CREATED',
        'PAYMENT_PENDING',
        'PAID',
        'CANCELLED'
        ) NOT NULL,
    payment_id VARCHAR(255),
    paid_at DATETIME,
    created_at DATETIME NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    item_total DOUBLE NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       phone VARCHAR(20) NOT NULL
);

CREATE TABLE dishes (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        price DOUBLE NOT NULL
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        status VARCHAR(50) NOT NULL CHECK (status IN ('NEW', 'PROCESSING', 'COMPLETED', 'CANCELLED')),
                        total_price DOUBLE NOT NULL,
                        FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE order_dishes (
                              order_id BIGINT NOT NULL,
                              dish_id BIGINT NOT NULL,
                              PRIMARY KEY (order_id, dish_id),
                              FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                              FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
);
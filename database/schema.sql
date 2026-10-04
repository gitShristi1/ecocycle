-- EcoCycle database schema (Oracle 10g Express Edition)
-- Run while connected as the ECOCYCLE user.
-- NOTE: SQL*Plus ends a statement at a blank line, so there are
-- no blank lines inside any statement below.

SET DEFINE OFF

-- ---------------------------------------------------------
-- Sequences (10g has no identity columns)
-- ---------------------------------------------------------
CREATE SEQUENCE seq_admins          START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_users           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_companies       START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_waste_types     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_waste_requests  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_products        START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_orders          START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_order_items     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_payments        START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_feedback        START WITH 1 INCREMENT BY 1;

-- ---------------------------------------------------------
-- Accounts
-- ---------------------------------------------------------
CREATE TABLE admins (
    admin_id       NUMBER(10)     PRIMARY KEY,
    full_name      VARCHAR2(100)  NOT NULL,
    email          VARCHAR2(150)  NOT NULL UNIQUE,
    password_hash  VARCHAR2(100)  NOT NULL,
    created_at     DATE           DEFAULT SYSDATE NOT NULL
);

CREATE TABLE users (
    user_id        NUMBER(10)     PRIMARY KEY,
    full_name      VARCHAR2(100)  NOT NULL,
    email          VARCHAR2(150)  NOT NULL UNIQUE,
    password_hash  VARCHAR2(100)  NOT NULL,
    phone          VARCHAR2(20),
    address        VARCHAR2(300),
    city           VARCHAR2(80),
    status         VARCHAR2(10)   DEFAULT 'ACTIVE' NOT NULL,
    created_at     DATE           DEFAULT SYSDATE NOT NULL,
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'BLOCKED'))
);

CREATE TABLE companies (
    company_id     NUMBER(10)     PRIMARY KEY,
    company_name   VARCHAR2(150)  NOT NULL,
    email          VARCHAR2(150)  NOT NULL UNIQUE,
    password_hash  VARCHAR2(100)  NOT NULL,
    phone          VARCHAR2(20),
    address        VARCHAR2(300),
    city           VARCHAR2(80),
    status         VARCHAR2(10)   DEFAULT 'PENDING' NOT NULL,
    created_at     DATE           DEFAULT SYSDATE NOT NULL,
    CONSTRAINT chk_companies_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'BLOCKED'))
);

-- ---------------------------------------------------------
-- Admin-controlled pricing
-- ---------------------------------------------------------
CREATE TABLE waste_types (
    waste_type_id  NUMBER(10)     PRIMARY KEY,
    type_name      VARCHAR2(50)   NOT NULL UNIQUE,
    rate_per_kg    NUMBER(8,2)    NOT NULL,
    active         CHAR(1)        DEFAULT 'Y' NOT NULL,
    CONSTRAINT chk_wt_rate   CHECK (rate_per_kg >= 0),
    CONSTRAINT chk_wt_active CHECK (active IN ('Y', 'N'))
);

CREATE TABLE platform_settings (
    setting_key    VARCHAR2(50)   PRIMARY KEY,
    setting_value  VARCHAR2(100)  NOT NULL
);

-- ---------------------------------------------------------
-- Waste requests and payments to users
-- (rate_per_kg is copied from waste_types at submit time, so later
--  rate changes do not alter old requests)
-- ---------------------------------------------------------
CREATE TABLE waste_requests (
    request_id      NUMBER(10)     PRIMARY KEY,
    user_id         NUMBER(10)     NOT NULL,
    waste_type_id   NUMBER(10)     NOT NULL,
    company_id      NUMBER(10),
    weight_kg       NUMBER(8,2)    NOT NULL,
    rate_per_kg     NUMBER(8,2)    NOT NULL,
    total_amount    NUMBER(10,2)   NOT NULL,
    pickup_address  VARCHAR2(300)  NOT NULL,
    city            VARCHAR2(80),
    notes           VARCHAR2(500),
    image_path      VARCHAR2(300),
    status          VARCHAR2(12)   DEFAULT 'SUBMITTED' NOT NULL,
    created_at      DATE           DEFAULT SYSDATE NOT NULL,
    accepted_at     DATE,
    picked_up_at    DATE,
    paid_at         DATE,
    CONSTRAINT fk_wr_user    FOREIGN KEY (user_id)       REFERENCES users (user_id),
    CONSTRAINT fk_wr_type    FOREIGN KEY (waste_type_id) REFERENCES waste_types (waste_type_id),
    CONSTRAINT fk_wr_company FOREIGN KEY (company_id)    REFERENCES companies (company_id),
    CONSTRAINT chk_wr_weight CHECK (weight_kg > 0),
    CONSTRAINT chk_wr_status CHECK (status IN
        ('SUBMITTED', 'ACCEPTED', 'PICKED_UP', 'PAID', 'REJECTED', 'CANCELLED'))
);

CREATE INDEX idx_wr_status ON waste_requests (status);
CREATE INDEX idx_wr_user   ON waste_requests (user_id);

CREATE TABLE payments (
    payment_id   NUMBER(10)    PRIMARY KEY,
    request_id   NUMBER(10)    NOT NULL UNIQUE,
    user_id      NUMBER(10)    NOT NULL,
    company_id   NUMBER(10)    NOT NULL,
    amount       NUMBER(10,2)  NOT NULL,
    paid_at      DATE          DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_pay_request FOREIGN KEY (request_id) REFERENCES waste_requests (request_id),
    CONSTRAINT fk_pay_user    FOREIGN KEY (user_id)    REFERENCES users (user_id),
    CONSTRAINT fk_pay_company FOREIGN KEY (company_id) REFERENCES companies (company_id)
);

-- ---------------------------------------------------------
-- Recycled products and orders
-- ---------------------------------------------------------
CREATE TABLE products (
    product_id    NUMBER(10)      PRIMARY KEY,
    company_id    NUMBER(10)      NOT NULL,
    product_name  VARCHAR2(150)   NOT NULL,
    description   VARCHAR2(1000),
    price         NUMBER(10,2)    NOT NULL,
    stock         NUMBER(8)       DEFAULT 0 NOT NULL,
    image_path    VARCHAR2(300),
    status        VARCHAR2(10)    DEFAULT 'ACTIVE' NOT NULL,
    created_at    DATE            DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_prod_company FOREIGN KEY (company_id) REFERENCES companies (company_id),
    CONSTRAINT chk_prod_price  CHECK (price >= 0),
    CONSTRAINT chk_prod_stock  CHECK (stock >= 0),
    CONSTRAINT chk_prod_status CHECK (status IN ('ACTIVE', 'REMOVED'))
);

CREATE INDEX idx_prod_company ON products (company_id);

CREATE TABLE orders (
    order_id          NUMBER(10)     PRIMARY KEY,
    user_id           NUMBER(10)     NOT NULL,
    total_amount      NUMBER(10,2)   NOT NULL,
    shipping_address  VARCHAR2(300)  NOT NULL,
    status            VARCHAR2(12)   DEFAULT 'PLACED' NOT NULL,
    created_at        DATE           DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_ord_user   FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT chk_ord_status CHECK (status IN ('PLACED', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

-- commission_percent / commission_amount are saved per item at sale time
CREATE TABLE order_items (
    order_item_id       NUMBER(10)    PRIMARY KEY,
    order_id            NUMBER(10)    NOT NULL,
    product_id          NUMBER(10)    NOT NULL,
    company_id          NUMBER(10)    NOT NULL,
    quantity            NUMBER(6)     NOT NULL,
    unit_price          NUMBER(10,2)  NOT NULL,
    commission_percent  NUMBER(5,2)   NOT NULL,
    commission_amount   NUMBER(10,2)  NOT NULL,
    CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders (order_id),
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_oi_company FOREIGN KEY (company_id) REFERENCES companies (company_id),
    CONSTRAINT chk_oi_qty    CHECK (quantity > 0)
);

CREATE INDEX idx_oi_order ON order_items (order_id);

-- ---------------------------------------------------------
-- Feedback (one review per user per product)
-- ---------------------------------------------------------
CREATE TABLE feedback (
    feedback_id  NUMBER(10)      PRIMARY KEY,
    product_id   NUMBER(10)      NOT NULL,
    user_id      NUMBER(10)      NOT NULL,
    rating       NUMBER(1)       NOT NULL,
    review_text  VARCHAR2(1000),
    created_at   DATE            DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_fb_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_fb_user    FOREIGN KEY (user_id)    REFERENCES users (user_id),
    CONSTRAINT chk_fb_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_fb_user_product UNIQUE (user_id, product_id)
);

-- ---------------------------------------------------------
-- Triggers: fill the primary key from the sequence on insert
-- ---------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_admins_bi
BEFORE INSERT ON admins
FOR EACH ROW
WHEN (NEW.admin_id IS NULL)
BEGIN
    SELECT seq_admins.NEXTVAL INTO :NEW.admin_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_users_bi
BEFORE INSERT ON users
FOR EACH ROW
WHEN (NEW.user_id IS NULL)
BEGIN
    SELECT seq_users.NEXTVAL INTO :NEW.user_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_companies_bi
BEFORE INSERT ON companies
FOR EACH ROW
WHEN (NEW.company_id IS NULL)
BEGIN
    SELECT seq_companies.NEXTVAL INTO :NEW.company_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_waste_types_bi
BEFORE INSERT ON waste_types
FOR EACH ROW
WHEN (NEW.waste_type_id IS NULL)
BEGIN
    SELECT seq_waste_types.NEXTVAL INTO :NEW.waste_type_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_waste_requests_bi
BEFORE INSERT ON waste_requests
FOR EACH ROW
WHEN (NEW.request_id IS NULL)
BEGIN
    SELECT seq_waste_requests.NEXTVAL INTO :NEW.request_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_products_bi
BEFORE INSERT ON products
FOR EACH ROW
WHEN (NEW.product_id IS NULL)
BEGIN
    SELECT seq_products.NEXTVAL INTO :NEW.product_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_orders_bi
BEFORE INSERT ON orders
FOR EACH ROW
WHEN (NEW.order_id IS NULL)
BEGIN
    SELECT seq_orders.NEXTVAL INTO :NEW.order_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_order_items_bi
BEFORE INSERT ON order_items
FOR EACH ROW
WHEN (NEW.order_item_id IS NULL)
BEGIN
    SELECT seq_order_items.NEXTVAL INTO :NEW.order_item_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_payments_bi
BEFORE INSERT ON payments
FOR EACH ROW
WHEN (NEW.payment_id IS NULL)
BEGIN
    SELECT seq_payments.NEXTVAL INTO :NEW.payment_id FROM dual;
END;
/

CREATE OR REPLACE TRIGGER trg_feedback_bi
BEFORE INSERT ON feedback
FOR EACH ROW
WHEN (NEW.feedback_id IS NULL)
BEGIN
    SELECT seq_feedback.NEXTVAL INTO :NEW.feedback_id FROM dual;
END;
/

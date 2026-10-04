-- EcoCycle starting data. Run after schema.sql, as the ECOCYCLE user.
-- Rates are per kg and can be changed later from the admin module.
-- The admin account is not created here, because its password must be
-- stored as a BCrypt hash. We will create it from Java in the login step.

INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('Plastic',   12);
INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('Paper',      8);
INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('Cardboard',  6);
INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('Metal',     25);
INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('Glass',      4);
INSERT INTO waste_types (type_name, rate_per_kg) VALUES ('E-Waste',   40);

INSERT INTO platform_settings (setting_key, setting_value) VALUES ('COMMISSION_PERCENT', '10');

COMMIT;

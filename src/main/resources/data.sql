USE infracciones_db;

INSERT IGNORE INTO tipos_infraccion (codigo, descripcion, monto_base) VALUES
('TI-001', 'Exceso de velocidad', 500.00),
('TI-002', 'Conducir en estado de ebriedad', 1500.00),
('TI-003', 'No usar cinturón de seguridad', 300.00),
('TI-004', 'Estacionamiento prohibido', 200.00),
('TI-005', 'Semáforo en rojo', 800.00);

INSERT IGNORE INTO infractores (dni, nombre, apellido, email, bloqueado) VALUES
('12345678', 'Juan', 'Pérez', 'juan.perez@mail.com', false),
('87654321', 'María', 'García', 'maria.garcia@mail.com', false),
('11223344', 'Carlos', 'López', 'carlos.lopez@mail.com', false);

INSERT IGNORE INTO vehiculos (placa, marca, anio, suspendido) VALUES
('ABC-123', 'Toyota', 2020, false),
('DEF-456', 'Honda', 2019, false),
('GHI-789', 'Nissan', 2021, false);

INSERT IGNORE INTO infractor_vehiculo (infractor_id, vehiculo_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(3, 3);
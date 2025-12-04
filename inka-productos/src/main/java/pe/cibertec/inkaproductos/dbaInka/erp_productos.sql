
DROP DATABASE IF EXISTS erp_productos;
CREATE DATABASE erp_productos CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE erp_productos;


-- TABLAS DE SEGURIDAD


CREATE TABLE rol (
    rol_id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (rol_id)
) ENGINE=InnoDB;

CREATE TABLE usuario (
    usuario_id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (usuario_id)
) ENGINE=InnoDB;

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (usuario_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (rol_id) REFERENCES rol (rol_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


-- CATÁLOGOS

CREATE TABLE categoria (
  categoria_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (categoria_id),
  UNIQUE KEY idx_categoria_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE unidad (
  unidad_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(10) NOT NULL,
  nombre VARCHAR(40) NOT NULL,
  factor_base DECIMAL(18,6) NOT NULL DEFAULT 1.000000,
  es_base TINYINT(1) NOT NULL DEFAULT 1,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (unidad_id),
  UNIQUE KEY idx_unidad_codigo (codigo)
) ENGINE=InnoDB;

CREATE TABLE producto (
  producto_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  sku VARCHAR(30) NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  descripcion VARCHAR(500) NULL,
  categoria_id SMALLINT UNSIGNED NOT NULL,
  codigo_barras VARCHAR(50) NULL,
  precio_lista DECIMAL(18,2) NULL,
  imagen_url VARCHAR(255) NULL, 
  activo TINYINT(1) NOT NULL DEFAULT 1,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (producto_id),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (categoria_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  UNIQUE KEY idx_producto_sku (sku),
  KEY idx_producto_nombre (nombre),
  KEY idx_producto_categoria (categoria_id)
) ENGINE=InnoDB;

CREATE TABLE producto_unidad (
  producto_id INT UNSIGNED NOT NULL,
  unidad_id SMALLINT UNSIGNED NOT NULL,
  factor DECIMAL(18,6) NOT NULL DEFAULT 1.000000,
  es_base TINYINT(1) NOT NULL DEFAULT 0,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (producto_id, unidad_id),
  CONSTRAINT fk_producto_unidad_producto FOREIGN KEY (producto_id) REFERENCES producto (producto_id) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_producto_unidad_unidad FOREIGN KEY (unidad_id) REFERENCES unidad (unidad_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_producto_unidad_factor CHECK (factor > 0),
  KEY idx_producto_unidad_base (producto_id, es_base)
) ENGINE=InnoDB;

CREATE TABLE almacen (
  almacen_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  tipo ENUM('PRINCIPAL','SECUNDARIO','TRANSITO') NOT NULL DEFAULT 'PRINCIPAL',
  direccion VARCHAR(180) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (almacen_id),
  UNIQUE KEY idx_almacen_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE inventario (
  almacen_id SMALLINT UNSIGNED NOT NULL,
  producto_id INT UNSIGNED NOT NULL,
  cantidad DECIMAL(18,6) NOT NULL DEFAULT 0, 
  stock_min DECIMAL(18,6) NOT NULL DEFAULT 0,
  stock_max DECIMAL(18,6) NULL,
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (almacen_id, producto_id),
  CONSTRAINT fk_inventario_almacen FOREIGN KEY (almacen_id) REFERENCES almacen (almacen_id) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES producto (producto_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_inventario_noneg CHECK (cantidad >= 0 AND stock_min >= 0),
  KEY idx_inventario_cantidad (cantidad)
) ENGINE=InnoDB;

CREATE TABLE inventario_movimiento (
  inventario_movimiento_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  almacen_id SMALLINT UNSIGNED NOT NULL,
  producto_id INT UNSIGNED NOT NULL,
  unidad_id SMALLINT UNSIGNED NOT NULL, 
  tipo_movimiento ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  cantidad DECIMAL(18,6) NOT NULL,
  costo_unitario DECIMAL(18,6) NULL,
  referencia VARCHAR(100) NULL,
  usuario VARCHAR(60) NULL, 
  ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (inventario_movimiento_id),
  CONSTRAINT fk_inv_mov_almacen FOREIGN KEY (almacen_id) REFERENCES almacen (almacen_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_inv_mov_producto FOREIGN KEY (producto_id) REFERENCES producto (producto_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_inv_mov_unidad FOREIGN KEY (unidad_id) REFERENCES unidad (unidad_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_inv_mov_cantidad CHECK (cantidad <> 0),
  KEY idx_inv_mov_fecha (fecha_movimiento),
  KEY idx_inv_mov_ap (almacen_id, producto_id)
) ENGINE=InnoDB;

CREATE OR REPLACE VIEW v_inventario_por_almacen AS
SELECT
  m.almacen_id,
  m.producto_id,
  SUM(
    CASE m.tipo_movimiento
      WHEN 'ENTRADA' THEN m.cantidad * u.factor_base
      WHEN 'SALIDA'  THEN -m.cantidad * u.factor_base
      WHEN 'AJUSTE'  THEN m.cantidad * u.factor_base 
    END
  ) AS stock_calculado
FROM inventario_movimiento m
JOIN unidad u ON m.unidad_id = u.unidad_id
GROUP BY m.almacen_id, m.producto_id;


-- ROLES Y USUARIOS
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('USER');

INSERT INTO usuario (nombre, email, password_hash, enabled) VALUES 
('Administrador Principal', 'admin@inka.com', '$2a$10$M9qdWivDupim72L.lJqwsu26v8RKD9HtOi8gXzb0V1C5LwbUjmgVe', 1),
('Usuario Supervisor', 'super@inka.com', '$2a$10$qXRFu8yJmyGhhnr3yKL51u6X595FJKckZJuhaW.zuvfd82KT7qlIm', 1),
('Operador Almacén Norte', 'norte@inka.com', '$2a$10$qXRFu8yJmyGhhnr3yKL51u6X595FJKckZJuhaW.zuvfd82KT7qlIm', 1),
('Usuario de Logística', 'logistica@inka.com', '$2a$10$qXRFu8yJmyGhhnr3yKL51u6X595FJKckZJuhaW.zuvfd82KT7qlIm', 1);

INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.usuario_id, r.rol_id FROM usuario u, rol r WHERE u.email='admin@inka.com' AND r.nombre='ADMIN';
INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.usuario_id, r.rol_id FROM usuario u, rol r WHERE u.email='admin@inka.com' AND r.nombre='USER';
INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.usuario_id, r.rol_id FROM usuario u, rol r WHERE u.email='super@inka.com' AND r.nombre='USER';

-- CATEGORÍAS
INSERT INTO categoria (nombre, descripcion) VALUES
('Electrónica', 'Dispositivos de consumo y accesorios tecnológicos'),
('Alimentos Secos', 'Comida no perecedera, granos, etc.'),
('Mobiliario', 'Muebles para hogar y oficina'),
('Limpieza', 'Productos químicos y herramientas de aseo'),
('Herramientas', 'Equipos de mano y maquinaria ligera');

-- UNIDADES
INSERT INTO unidad (codigo, nombre, factor_base, es_base) VALUES
('UND', 'Unidad', 1.000000, 1),
('KG', 'Kilogramo', 1.000000, 1),
('LT', 'Litro', 1.000000, 1),
('CAJA12', 'Caja (12 unidades)', 12.000000, 0),
('PALLET', 'Pallet', 1000.000000, 0);

-- ALMACENES
INSERT INTO almacen (nombre, tipo, direccion, activo) VALUES
('Almacén Central Lima', 'PRINCIPAL', 'Av. Javier Prado Este 1500, Lima', 1),
('Almacén de Tránsito Sur', 'TRANSITO', 'Km 50 Panamericana Sur, Ica', 1),
('Almacén Secundario Arequipa', 'SECUNDARIO', 'Calle Real 201, Arequipa', 1),
('Almacén de Herramientas', 'SECUNDARIO', 'Av. Industrial 800, Huancayo', 1),
('Almacén Virtual', 'PRINCIPAL', 'N/A', 0);

-- PRODUCTOS
INSERT INTO producto (sku, nombre, descripcion, categoria_id, codigo_barras, precio_lista, activo) VALUES
('LAP-DELL-X13', 'Laptop Ultrabook XPS 13', 'Portátil de alto rendimiento.', 1, '775123450001', 4899.99, 1), -- ID 1
('SNK-CHOC-100', 'Barra de Chocolate 100g', 'Chocolate de leche con almendras.', 2, '775123450002', 4.50, 1), -- ID 2
('HMT-TALAD', 'Taladro Percutor 850W', 'Taladro profesional con velocidad variable.', 5, '775123450003', 250.00, 1), -- ID 3
('MBL-MESA', 'Mesa de Escritorio Minimalista', 'Escritorio robusto de 120x60cm.', 3, '775123450004', 350.00, 1), -- ID 4
('CLE-PISOS', 'Detergente Concentrado 1L', 'Fórmula de alta espuma para pisos.', 4, '775123450005', 18.00, 1), -- ID 5
('SNK-ARROZ-5', 'Arroz Superior 5KG', 'Bolsa de arroz grano largo.', 2, '775123450006', 22.50, 1), -- ID 6
('LAP-ACC-MOU', 'Mouse Inalámbrico Ergonómico', 'Mouse de precisión.', 1, '775123450007', 85.00, 1), -- ID 7
('HMT-LLAVE', 'Set de Llaves Combinadas (20 pcs)', 'Juego completo de llaves.', 5, '775123450008', 150.00, 1), -- ID 8
('CLE-DESINF', 'Desinfectante Aerosol 400ml', 'Elimina 99.9% de bacterias.', 4, '775123450009', 12.50, 1), -- ID 9
('MBL-ESTAN', 'Estante de Almacenamiento Metálico', 'Estantería modular de 4 niveles.', 3, '775123450010', 80.00, 1); -- ID 10

INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (1, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (2, 1, 1.0, 1), (2, 4, 12.0, 0);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (3, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (4, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (5, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (6, 2, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (7, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (8, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (9, 1, 1.0, 1);
INSERT INTO producto_unidad (producto_id, unidad_id, factor, es_base) VALUES (10, 1, 1.0, 1);


INSERT INTO inventario (almacen_id, producto_id, cantidad, stock_min, stock_max) VALUES
(1, 1, 100.00, 10.0, 500.0), 
(1, 2, 100.00, 10.0, 500.0), 
(1, 3, 100.00, 10.0, 500.0), 
(1, 4, 100.00, 10.0, 500.0),
(1, 5, 100.00, 10.0, 500.0), 
(1, 6, 100.00, 10.0, 500.0),
(1, 7, 100.00, 10.0, 500.0),
(1, 8, 100.00, 10.0, 500.0),
(1, 9, 100.00, 10.0, 500.0),
(1, 10, 100.00, 10.0, 500.0); 

INSERT INTO inventario_movimiento (almacen_id, producto_id, unidad_id, tipo_movimiento, cantidad, costo_unitario, referencia, usuario) VALUES
(1, 1, 1, 'ENTRADA', 30.0, 4000.00, 'OC-LPT-001', 'logistica@inka.com'),
(1, 7, 1, 'ENTRADA', 150.0, 60.00, 'OC-MOU-001', 'logistica@inka.com'),
(1, 2, 1, 'ENTRADA', 60.0, 3.50, 'OC-CHO-001', 'logistica@inka.com'),
(1, 1, 1, 'SALIDA', 2.0, 30.00, 'VTA-C001', 'logistica@inka.com'),
(1, 7, 1, 'SALIDA', 50.0, 60.00, 'TRF-LMA-ICA-01', 'logistica@inka.com'),
(2, 7, 1, 'ENTRADA', 50.0, 60.00, 'TRF-LMA-ICA-01', 'logistica@inka.com'),
(4, 3, 1, 'ENTRADA', 25.0, 200.00, 'OC-HERR-001', 'logistica@inka.com'),
(1, 2, 1, 'SALIDA', 5.0, 3.50, 'AJT-ROT-001', 'super@inka.com'),
(2, 1, 1, 'ENTRADA', 1.0, 4500.00, 'AJT-ENF-001', 'norte@inka.com');
-- redes_schema.sql
-- Esquema inicial para la base de datos `redes` y datos de ejemplo
-- Ejecutar como un usuario con privilegios (ej. root):
--   mysql -u root -p < redes_schema.sql

-- 1) Crear la base de datos
CREATE DATABASE redes;
USE redes;

-- 3) Tablas principales

-- Usuarios (base para autenticacion)
CREATE TABLE IF NOT EXISTS usuarios (
  id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  email VARCHAR(200) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  rol VARCHAR(50) NOT NULL DEFAULT 'PASAJERO',
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Administradores (perfil)
CREATE TABLE IF NOT EXISTS administradores (
  id_admin BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL UNIQUE,
  area VARCHAR(100),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_admin_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Pasajeros (perfil)
CREATE TABLE IF NOT EXISTS pasajeros (
  id_pasajero BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL UNIQUE,
  telefono VARCHAR(30),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pasajero_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Conductores (info específica de conductores)
CREATE TABLE IF NOT EXISTS conductores (
  id_conductor BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL UNIQUE,
  licencia VARCHAR(100) NOT NULL,
  vehiculo VARCHAR(150),
  calificacion_promedio DECIMAL(3,2) DEFAULT 0,
  disponible BOOLEAN DEFAULT TRUE,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_conductor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Viajes
CREATE TABLE IF NOT EXISTS viajes (
  id_viaje BIGINT AUTO_INCREMENT PRIMARY KEY,
  pasajero_id BIGINT NOT NULL,
  conductor_id BIGINT NULL,
  origen_lat DECIMAL(10,7) NOT NULL,
  origen_lng DECIMAL(10,7) NOT NULL,
  destino_lat DECIMAL(10,7) NOT NULL,
  destino_lng DECIMAL(10,7) NOT NULL,
  estado VARCHAR(50) NOT NULL DEFAULT 'SOLICITADO',
  precio DECIMAL(10,2) DEFAULT 0,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_viaje_pasajero FOREIGN KEY (pasajero_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
  CONSTRAINT fk_viaje_conductor FOREIGN KEY (conductor_id) REFERENCES conductores(id_conductor) ON DELETE SET NULL
);

-- Calificaciones
CREATE TABLE IF NOT EXISTS calificaciones (
  id_calificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
  viaje_id BIGINT NOT NULL,
  calificador_id BIGINT NOT NULL,
  puntuacion TINYINT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
  comentario TEXT,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_calificacion_viaje FOREIGN KEY (viaje_id) REFERENCES viajes(id_viaje) ON DELETE CASCADE,
  CONSTRAINT fk_calificacion_usuario FOREIGN KEY (calificador_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_viaje_estado ON viajes(estado);
CREATE INDEX IF NOT EXISTS idx_conductor_disponible ON conductores(disponible);

-- 4) Datos de ejemplo (útiles para pruebas locales)
INSERT INTO usuarios (nombre, email, password, rol) VALUES
  ('Administrador', 'admin@local.test', 'admin-password-hash', 'ADMIN'),
  ('Pasajero Uno', 'pasajero1@local.test', 'pasajero-password-hash', 'PASAJERO'),
  ('Conductor Uno', 'conductor1@local.test', 'conductor-password-hash', 'CONDUCTOR');

INSERT INTO administradores (usuario_id, area) VALUES
  (1, 'Operaciones');

INSERT INTO pasajeros (usuario_id, telefono) VALUES
  (2, '900000000');

INSERT INTO conductores (usuario_id, licencia, vehiculo, calificacion_promedio, disponible) VALUES
  (3, 'ABC-12345', 'Toyota Prius - PLACA123', 4.8, TRUE);

-- Ejemplo de calificación (se añadirá una vez finalizado el viaje)
-- INSERT INTO calificaciones (viaje_id, calificador_id, puntuacion, comentario) VALUES (1, 2, 5, 'Excelente servicio');

-- 5) Notas
-- - Cambia las contraseñas de ejemplo por valores seguros antes de producción.
-- - Si el servidor MySQL usa autenticación por caching_sha2_password, puede ser necesario
--   crear el usuario con el plugin adecuado o usar la opción `ALTER USER ... IDENTIFIED WITH 'caching_sha2_password' BY '...'`.
-- - Para ejecutar desde PowerShell (en Windows) asegúrate de ejecutar desde una consola con permisos y tener el binario `mysql` en el PATH, o usa la ruta completa:
--     "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < db\redes_schema.sql

-- Fin de archivo

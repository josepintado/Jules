-- Tabla para los roles de usuario
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla para los usuarios
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    role_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Tabla para los documentos principales
CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_type VARCHAR(100),
    size_in_bytes BIGINT,
    digital_signature TEXT, -- Para almacenar la firma digital
    is_digitized BOOLEAN DEFAULT TRUE,
    created_by_user_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users (id)
);

-- Tabla para los metadatos del documento, vinculada al proceso de registro
CREATE TABLE document_metadata (
    id SERIAL PRIMARY KEY,
    document_id INTEGER UNIQUE NOT NULL,
    motivo TEXT,
    fecha_registro DATE,
    hora_registro TIME,
    ubicacion VARCHAR(255),
    observacion TEXT,
    FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE CASCADE
);

-- Insertar roles iniciales
INSERT INTO roles (name) VALUES ('JEFE_ARCHIVO'), ('AREA_SOLICITANTE'), ('USUARIO_EXTERNO');

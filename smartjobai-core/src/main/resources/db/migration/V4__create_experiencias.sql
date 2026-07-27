CREATE TABLE experiencias (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    empresa VARCHAR(255) NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    atual BOOLEAN NOT NULL DEFAULT FALSE,
    descricao VARCHAR(3000),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_experiencia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_experiencia_usuario ON experiencias (usuario_id);
CREATE INDEX idx_experiencia_cargo ON experiencias (cargo);

CREATE TABLE experiencia_tecnologias (
    experiencia_id BIGINT NOT NULL,
    tecnologia VARCHAR(100) NOT NULL,
    CONSTRAINT fk_exp_tecnologia_experiencia FOREIGN KEY (experiencia_id) REFERENCES experiencias (id) ON DELETE CASCADE
);

CREATE INDEX idx_exp_tecnologia ON experiencia_tecnologias (tecnologia);

CREATE TABLE experiencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    empresa VARCHAR(255) NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    atual BOOLEAN NOT NULL DEFAULT FALSE,
    descricao VARCHAR(3000),
    data_criacao DATETIME NOT NULL,
    data_atualizacao DATETIME,
    CONSTRAINT fk_experiencia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_experiencia_usuario ON experiencias (usuario_id);
CREATE INDEX idx_experiencia_cargo ON experiencias (cargo);

CREATE TABLE experiencia_tecnologias (
    experiencia_id BIGINT NOT NULL,
    tecnologia VARCHAR(100) NOT NULL,
    CONSTRAINT fk_exp_tecnologia_experiencia FOREIGN KEY (experiencia_id) REFERENCES experiencias (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_exp_tecnologia ON experiencia_tecnologias (tecnologia);

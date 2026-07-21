CREATE TABLE habilidades_tecnicas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    nivel_proficiencia VARCHAR(20) NOT NULL,
    anos_experiencia INT,
    data_criacao DATETIME NOT NULL,
    data_atualizacao DATETIME,
    CONSTRAINT fk_habilidade_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_habilidade_usuario ON habilidades_tecnicas (usuario_id);
CREATE INDEX idx_habilidade_nome ON habilidades_tecnicas (nome);

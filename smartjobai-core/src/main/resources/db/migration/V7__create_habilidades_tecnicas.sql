CREATE TABLE habilidades_tecnicas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    nivel_proficiencia VARCHAR(20) NOT NULL,
    anos_experiencia INT,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_habilidade_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_habilidade_usuario ON habilidades_tecnicas (usuario_id);
CREATE INDEX idx_habilidade_nome ON habilidades_tecnicas (nome);

CREATE TABLE idiomas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_idioma_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_idioma_usuario ON idiomas (usuario_id);

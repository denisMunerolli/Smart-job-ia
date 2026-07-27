CREATE TABLE curriculos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(255),
    versao INT NOT NULL DEFAULT 1,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    conteudo_json TEXT,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_curriculo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_curriculo_usuario ON curriculos (usuario_id);

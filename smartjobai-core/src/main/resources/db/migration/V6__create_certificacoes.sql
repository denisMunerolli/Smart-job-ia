CREATE TABLE certificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    instituicao_emissora VARCHAR(255) NOT NULL,
    data_emissao DATE,
    data_expiracao DATE,
    credencial_url VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_certificacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_certificacao_usuario ON certificacoes (usuario_id);

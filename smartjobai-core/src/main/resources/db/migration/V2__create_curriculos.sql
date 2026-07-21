CREATE TABLE curriculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(255),
    versao INT NOT NULL DEFAULT 1,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    conteudo_json LONGTEXT,
    data_criacao DATETIME NOT NULL,
    data_atualizacao DATETIME,
    CONSTRAINT fk_curriculo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_curriculo_usuario ON curriculos (usuario_id);

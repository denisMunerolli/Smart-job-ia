CREATE TABLE formacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    instituicao VARCHAR(255) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    nivel VARCHAR(30) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    em_andamento BOOLEAN NOT NULL DEFAULT FALSE,
    descricao VARCHAR(2000),
    data_criacao DATETIME NOT NULL,
    data_atualizacao DATETIME,
    CONSTRAINT fk_formacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_formacao_usuario ON formacoes (usuario_id);

CREATE TABLE idiomas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_atualizacao DATETIME,
    CONSTRAINT fk_idioma_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_idioma_usuario ON idiomas (usuario_id);

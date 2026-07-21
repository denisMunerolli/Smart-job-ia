CREATE TABLE vagas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_externo VARCHAR(255),
    fonte VARCHAR(50),
    titulo VARCHAR(255),
    empresa VARCHAR(255),
    descricao LONGTEXT,
    localizacao VARCHAR(255),
    data_coleta DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_vaga_fonte_id_externo ON vagas (fonte, id_externo);

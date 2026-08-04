CREATE TABLE vagas (
    id              BIGSERIAL PRIMARY KEY,
    id_externo      VARCHAR(255),
    fonte           VARCHAR(50),
    titulo          VARCHAR(255),
    empresa         VARCHAR(255),
    descricao       TEXT,
    localizacao     VARCHAR(255),
    data_coleta     TIMESTAMP,
    data_criacao    TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP
);

CREATE INDEX idx_vaga_fonte_id_externo ON vagas (fonte, id_externo);

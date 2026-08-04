CREATE TABLE candidaturas (
    id               BIGSERIAL PRIMARY KEY,
    usuario_id       BIGINT NOT NULL,
    vaga_id          BIGINT NOT NULL,
    curriculo_id     BIGINT,
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    observacao       TEXT,
    data_criacao     TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_candidatura_usuario  FOREIGN KEY (usuario_id)   REFERENCES usuarios(id)   ON DELETE CASCADE,
    CONSTRAINT fk_candidatura_vaga     FOREIGN KEY (vaga_id)      REFERENCES vagas(id)      ON DELETE CASCADE,
    CONSTRAINT fk_candidatura_curriculo FOREIGN KEY (curriculo_id) REFERENCES curriculos(id) ON DELETE SET NULL,
    CONSTRAINT uq_candidatura_usuario_vaga UNIQUE (usuario_id, vaga_id)
);

CREATE INDEX idx_candidatura_usuario ON candidaturas (usuario_id);
CREATE INDEX idx_candidatura_status  ON candidaturas (usuario_id, status);

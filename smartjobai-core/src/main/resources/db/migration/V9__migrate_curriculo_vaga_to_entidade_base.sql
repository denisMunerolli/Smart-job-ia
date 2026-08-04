-- Fase 3: migra curriculos e vagas para o padrão EntidadeBase
-- (data_criacao + data_atualizacao gerenciados pelo Hibernate/Flyway)

-- curriculos: data_criacao já existe; garante data_atualizacao
ALTER TABLE curriculos
    ALTER COLUMN data_criacao SET NOT NULL,
    ALTER COLUMN data_criacao SET DEFAULT NOW();

ALTER TABLE curriculos
    ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP;

UPDATE curriculos SET data_atualizacao = data_criacao WHERE data_atualizacao IS NULL;

-- vagas: adiciona as colunas de auditoria (a tabela não as tinha)
ALTER TABLE vagas
    ADD COLUMN IF NOT EXISTS data_criacao    TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP;

UPDATE vagas SET data_atualizacao = data_criacao WHERE data_atualizacao IS NULL;

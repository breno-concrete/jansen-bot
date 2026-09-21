CREATE TABLE ensaio (
    id               VARCHAR(64)  PRIMARY KEY,
    data_hora        VARCHAR(64)  NOT NULL,
    local            VARCHAR(255) NOT NULL,
    criado_em        TIMESTAMPTZ  NOT NULL,
    prazo_votacao_em TIMESTAMPTZ  NOT NULL,
    status           VARCHAR(32)  NOT NULL,
    decisao_final    VARCHAR(32)  NOT NULL
);

CREATE TABLE ensaio_remarcacao (
    ensaio_id    VARCHAR(64) NOT NULL REFERENCES ensaio (id) ON DELETE CASCADE,
    data_hora    VARCHAR(64) NOT NULL,
    remarcado_em TIMESTAMPTZ NOT NULL
);

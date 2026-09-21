CREATE TABLE voto (
    ensaio_id     VARCHAR(64) NOT NULL REFERENCES ensaio (id) ON DELETE CASCADE,
    integrante_id VARCHAR(32) NOT NULL,
    escolha       VARCHAR(32) NOT NULL,
    respondido_em TIMESTAMPTZ,
    PRIMARY KEY (ensaio_id, integrante_id)
);

-- FR-019: tipo do ensaio. Ensaios já existentes eram todos "gerais" (sem filtro por tipo).
ALTER TABLE ensaio ADD COLUMN tipo VARCHAR(32) NOT NULL DEFAULT 'GERAL';
ALTER TABLE ensaio ALTER COLUMN tipo DROP DEFAULT;

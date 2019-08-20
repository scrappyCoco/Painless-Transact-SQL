--
-- Example 1: If before WITH (CTE) BEGIN presented - it's valid.
--
SELECT 'Something'
BEGIN
    -- There can be placed multiple comments.
    -- There can be placed multiple comments.
    WITH T AS (SELECT 1 AS A),
         G AS (SELECT * FROM T)
    SELECT *
    FROM G
END
GO
--
-- Example 2: In the beginning of procedure.
--
CREATE PROCEDURE P2
AS
    -- There can be placed multiple comments.
    -- There can be placed multiple comments.
WITH T AS (SELECT 1 AS A),
     G AS (SELECT * FROM T)
SELECT *
FROM G
GO
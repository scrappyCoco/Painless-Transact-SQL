--
-- Example 1: Semicolon is forgotten
--
SELECT 'Something'
-- There must be semicolon.
/*
 Another one comment.
 */

WITH T AS (SELECT 1 AS A),
     G AS (SELECT * FROM T)
SELECT *
FROM G
GO
DECLARE @T TABLE (D VARCHAR(2))
INSERT INTO @T (D)
VALUES (',,')

SELECT *
FROM @T AS T
CROSS APPLY STRING_SPLIT('1,2,3', T.D)
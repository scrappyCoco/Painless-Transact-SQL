DECLARE @D DATE;
DECLARE @I INT;
DECLARE @T TABLE (Id UNIQUEIDENTIFIER, Name Date);

INSERT INTO @T (id, name)
SELECT @D, @D
UNION ALL
SELECT @D, @I
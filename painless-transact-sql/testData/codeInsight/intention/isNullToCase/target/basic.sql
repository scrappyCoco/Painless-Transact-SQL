DECLARE @i VARCHAR(100)

SELECT CASE WHEN @i IS NULL THEN '123' ELSE @i END;
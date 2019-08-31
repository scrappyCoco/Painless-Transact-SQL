DECLARE @i INT;
SELECT <caret>IIF(@i <> 0, 'Green', 'Red');
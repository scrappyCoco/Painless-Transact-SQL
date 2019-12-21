CREATE PROCEDURE dbo.MyProc @i INT, @d DATE
AS
BEGIN
    SELECT i = @i, d = @d;
END
GO

DECLARE @i INT;
EXEC dbo.MyProc @i, @i;
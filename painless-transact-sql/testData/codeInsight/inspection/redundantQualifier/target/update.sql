CREATE TABLE dbo.TableOne (Id INT, Name VARCHAR(50));
CREATE TABLE dbo.TableTwo (Id INT, Name VARCHAR(50));

UPDATE dbo.TableOne
SET Name = TableTwo.Name
FROM dbo.TableOne
INNER JOIN dbo.TableTwo ON TableOne.Id = TableTwo.Id;
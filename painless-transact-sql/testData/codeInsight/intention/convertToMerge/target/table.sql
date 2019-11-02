CREATE TABLE dbo.MySource
(
    Id   INT          NOT NULL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL
);

CREATE TABLE dbo.MyTarget
(
    Id      INT          NOT NULL PRIMARY KEY,
    Name    VARCHAR(100) NOT NULL,
    AddDate DATETIME
);
GO

MERGE dbo.MyTarget AS Target
USING (
    SELECT Id      = Id,
           Name    = Name,
           AddDate = NULL
    FROM dbo.MySource
) AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED BY TARGET THEN
    INSERT (Id, Name, AddDate)
    VALUES (Id, Name, AddDate)
WHEN NOT MATCHED BY SOURCE THEN DELETE
WHEN MATCHED THEN
    UPDATE
    SET Name    = Source.Name,
        AddDate = Source.AddDate;
;
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

SELECT <caret>*
FROM dbo.MySource AS Source
    INNER JOIN dbo.MyTarget AS Target
ON Source.Id = Target.Id;
DECLARE @target TABLE
                (
                    Id   INT NOT NULL
                        PRIMARY KEY,
                    Name VARCHAR(100)
                );

DECLARE @source TABLE
                (
                    Id   INT NOT NULL
                        PRIMARY KEY,
                    Name VARCHAR(100)
                );

DECLARE @temp TABLE
              (
                  Id   INT NOT NULL
                      PRIMARY KEY,
                  Name VARCHAR(100)
              );

-- 1.
INSERT INTO @target
VALUES (1, '');

-- 2.
INSERT INTO @target (Id, Name)
SELECT *
FROM @source;

-- 3.
INSERT INTO @target (Id, Name)
OUTPUT inserted.Id, inserted.Name INTO @temp
SELECT Id,
       Name
FROM @source;

-- 4.
DELETE
FROM @target
OUTPUT deleted.Id, deleted.Name INTO @temp;

-- 5.
DELETE Target
OUTPUT deleted.Id, deleted.Name INTO @temp
FROM @source AS Source
         INNER JOIN @target AS Target ON Source.Id = Target.Id

-- 6.
MERGE @target AS Target
USING @source AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED THEN
    INSERT
    VALUES (Id, Name);

-- 7.
MERGE @target AS Target
USING @source AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED THEN
    INSERT (Id, Name)
    VALUES (Id, Name) OUTPUT * INTO @temp;

-- ignore 1.
INSERT INTO @target (Id, Name)
SELECT Id,
       Name
FROM (
         SELECT *
         FROM @source
     ) AS G;

-- ignore 2.
MERGE @target AS Target
USING (
    SELECT *
    FROM @source
) AS Source
ON Source.Id = Target.Id
WHEN MATCHED THEN DELETE;

-- ... INTO ... !()
-- MERGE ... INSERT !()

-- OUTPUT * INTO ...
-- INSERT INTO ... SELECT *

-- Quick fixes:
-- 1. Replace * to 1, 2, 3, ...
-- 2. Add column list (1, 2, 3, ...)
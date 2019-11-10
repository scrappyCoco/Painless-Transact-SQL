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

MERGE @target AS Target
USING @source AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED THEN
    INSERT
    VALUES (Id, Name);
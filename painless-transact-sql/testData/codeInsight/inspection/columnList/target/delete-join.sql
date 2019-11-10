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

DELETE Target
OUTPUT deleted.Id, deleted.Name INTO @temp (Id, Name)
FROM @source AS Source
         INNER JOIN @target AS Target ON Source.Id = Target.Id
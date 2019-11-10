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

INSERT INTO @target (Id, Name)
OUTPUT inserted.Id, inserted.Name INTO @temp (Id, Name)
SELECT Id,
       Name
FROM @source;
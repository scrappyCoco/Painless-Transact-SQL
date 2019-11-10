DECLARE @target TABLE
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

DELETE
FROM @target
OUTPUT deleted.Id, deleted.Name INTO @temp;
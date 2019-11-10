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

INSERT INTO @target (Id, Name)
SELECT Id,
       Name
FROM (
         SELECT *
         FROM @source
     ) AS G;
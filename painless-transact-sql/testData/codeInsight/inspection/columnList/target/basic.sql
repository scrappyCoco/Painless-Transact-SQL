DECLARE @target TABLE
                (
                    Id   INT NOT NULL
                        PRIMARY KEY,
                    Name VARCHAR(100)
                );

INSERT INTO @target(Id, Name)
VALUES (1, '');
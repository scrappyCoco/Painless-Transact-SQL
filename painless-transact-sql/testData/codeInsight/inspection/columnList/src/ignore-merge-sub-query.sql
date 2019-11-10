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

MERGE @target AS Target
USING (
    SELECT *
    FROM @source
) AS Source
ON Source.Id = Target.Id
WHEN MATCHED THEN DELETE;
DECLARE @T TABLE
           (
               A INT,
               B BIT,
               C CHAR
           );

INSERT INTO @T (A, B, C)
<caret>VALUES (1, 1, 'C'),
       (2, 0, 'A');
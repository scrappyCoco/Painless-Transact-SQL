DECLARE @d CHAR(2) = ';;'
SELECT value
FROM STRING_SPLIT('1,2,3', @d);

1. För att skapa en ny databas, gör så här.

1. Kör scriptet tables.ascii.sql. Om du kör mot en nyskapad db utan tabeller i, kommer
scriptet att skrika att det inte kan hitta tabellerna. Helt OK.

2. Kör scriptet sql/data/types.sql.

3. Kör scriptet sprocs.ascii.sql. Scriptet gnäller att den inte kan addera rader i
sysdepends. Ok.

4. Om det är en nyskapad db. Kör även scriptet sql/data/newdb.sql. Det adderar
in den första rollen, resp användaren., mallgruppen, mallen etc. Det som
behövs för att komma igång med databasen.
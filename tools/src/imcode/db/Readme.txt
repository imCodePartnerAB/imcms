Köra avumling:
DeUmlaut.java avumlar tabellen text.

Ställ in rätt parametrar för databasen i DeUmlaut.java
Kompilera med:  ant clean tools
Ställ dig i imCMS/1.3/tools/build mappen och skriv:

java -classpath .;..\..\install\lib\Opta2000.jar;..\..\install\lib\jdbc2_0-stdext.jar imcode.db.DeUmlaut login password database server

ex.
java -classpath .;..\..\install\lib\Opta2000.jar;..\..\install\lib\jdbc2_0-stdext.jar imcode.db.DeUmlaut sa nonac imcms lennart


ex. när filen ligger i webapp mappen client ändras sökvägen i classpathen enligt nedan.

java -classpath .;..\..\lib\Opta2000.jar;..\..\lib\jdbc2_0-stdext.jar imcode.db.DeUmlaut sa hwv62v3h db010602 localhost


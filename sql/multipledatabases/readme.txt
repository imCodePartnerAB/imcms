Skripten i denna katalog körs av klassen DatabaseService när man instantierar denna.
Se den klassen för att se vilka databaser som stödjs.
(I skrivandets stund är det SQLServer, Mimer och MySQL)

Vid förändring av skripen i denna katalog, se till att validera innehållet mot SQL 92/99 i möjligaste mån,
http://developer.mimer.com/validator/parser92/index.tml
http://developer.mimer.com/validator/parser92/index.tml
Se även till att alla tester genom testklassen TestDatabaseService fortsätter att gå att köra.

För att kunna skapa DatabaseService objectet behöver man en tom databas.
Se respektive databasleverantörs instruktioner för hur man gör detta.
Mimer: Kör ett skript med kommandot CREATE DATABANK innan create.sql skriptet körs. Se mimer.sql.
Räcker med att köra när databasen är nyskapat, en gång.

Kvar att undersöka/göra
* Default värden satta till NULL är borttagna
* Andra default värden är inte satta (ännu, går det, finns det en standard?)
* Indexeringen är droppad så länge men borde gå att lägga till.
* Kvar är help.sql tills jag vet mer hur dessa skapats.

Nedan är förändringar mot scriptet tables.ascii.sql
* Splittat i två separata skript. Ett för drop table och ett för create table.
* Satt in ; i slutet av varje kommando. (Standard SQL).
* tinyint är bytt mot smallint i alla tabeller
* Microsofts (och MySQL) "datetime" & "smalldatetime" har bytts ut mot "timestamp" (Då detta är Standard SQL, vid körning av create table commandon
 byts alla ut mot datetime innan de körs. Därefter går det att arbeta på vanligt sätt med jdbc även mot SQLServer)

När det gäller strängar skiljer sig dom åt rätt reält mellan databaserna:
- MySQL tar bort trailing spaces på CHAR vilket gör att jag kör VARCHAR genomgående för att få samma beteende från samtliga.
- Maximala storleken varierar också:
    MIMER VARCHAR(max 15000) därefter CLOB, samt CHAR VARYING(max 5000) därefter CLOB
    SQL Server VARCHAR(max 8 000) därefter text/ntext
    MySQL VARCHAR(max 255) därefter TEXT(65 535), MEDIUMTEXT(16 777 215), and LONGTEXT(4 294 967 295)
  CLOB fanns inget stöd för i MySQL eller i SQLServer så denna typ undveks då läsning och skrivning hade blivit olika för
  de olika databasfallen. Om inte VARCHAR(15000) räcker får vi ta en ny funderare.
  Då får man i så fall behandla MIMER annorlunda och använda CLOB för den och köra TEXT i övrigt.
Detta har lett till följande:
* meta: meta_text varchar(1000) -> TEXT i MySQL i övrigt oförändrat.
* frameset_docs: frame_set text -> VARCHAR(15000) i scriptet, som i sin tur byts ut mot TEXT i MySQL och i SQLServer
* texts: text ntext -> NCHAR VARYING(5000) i scriptet, som i sin tur byts ut mot TEXT i MySQL och NTEXT SQLServer

* I user tabellen är namnet 'external' bytt mot external_user (extern är ett reserverat ord i Standard SQL)
* I browsers tabellen är namnet 'value' bytt mot 'browser_value' (value är ett reserverat ord i Standard SQL)
* I sys_data tabellen är namnet 'value' bytt mot 'sysdata_value' (value är ett reserverat ord i Standard SQL)
* Bytte ut CAST( URRENT_TIME AS CHAR(80)) -> CAST( CURRENT_TIME AS CHAR) kodmässigt då MySQL inte stödde castning CHAR(siffror).
För att slippa göra trim på strängar i koden ändrade jag de (få) ställena med char till varchar. Detta för att MySQL trimmar alla
CHAR default, och det går inte att stänga av, så för att garanterat få samma beteende gjorde jag detta.
* CREATE TABLE lang_prefixes, lang_prefix char(3),
* CREATE TABLE roles, role_name char (25) NOT NULL
* CREATE TABLE user_types, type_name char (30) och lang_prefix char (3) NOT NULL ,

Förändringar gentemot filen types.sql
* Satt in ; i slutet på varje commando.
* 'value' bytt mot 'browser_value' på alla ställen som håller på med 'browsers' tabellen
* tog bort alla "SET IDENTITY_INSERT sys_types ON/OFF" då de inte behövs när det finns en primärnyckel.
* tog bort de bortkommenterade raderna:
    --INSERT INTO doc_types VALUES(101, 'se', 'Diagram');
    --INSERT INTO doc_types VALUES(101, 'en', 'Diagram');

Förändringar mot newdb.sql
* Satt in ; i slutet på varje commando.
* tog bort alla "SET IDENTITY_INSERT sys_types ON/OFF" då de inte behövs när det finns en primärnyckel.
* I sys_data tabellen är 'value' bytt mot 'sysdata_value'
* La till en siffra i för primary key i text kollumnen
* Ändrade getDate() till CURRENT_TIMESTAMP i users
* Ändrade getDate() till CURRENT_TIMESTAMP och droppade formateringen i sys_data! Kolla upp vart denna används, id = 2
* Ändrade getDate() till CURRENT_TIMESTAMP i meta


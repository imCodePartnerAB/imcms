Skripten i denna katalog körs av DatabaseService när man instantierar denna klass.
Se den klassen för att se vilka databaser som stödjs.
(I skrivandets stund är det SQLServer, Mimer och MySQL)

Vid förändring av skripen i denna katalog, se till att validera innehållet mot SQL 92,
http://developer.mimer.com/validator/parser92/index.tml

Innan man kör skripten måste man skapa en tom databas. Se respektive databasleverantörs instruktioner för hur man gör detta.
Mimer: Kör ett skript med kommandot CREATE DATABANK innan create.sql skriptet körs. Se mimer.sql.

Kvar att undersöka/göra
* Default värden satta till NULL är borttagna
* Andra default värden är inte satta (ännu, går det, finns det en standard?)
* Microsofts text & ntext (unicode variant av text) utbytt mot VARCHAR(255), MySQL stödjer inte större. Alternativ? CLOB?
* CREATE TABLE meta, meta_text varchar (1000) -> varchar (255)
* CREATE TABLE user_flags, description varchar (256) - varchar (255)
* CREATE TABLE frameset_docs, varchar(8000) -> varchar (255)
* CREATE TABLE texts, text varchar(8000) -> varchar (255)
* Microsofts Indexeringen är droppad, skapa annan? Verkar ingå i standardsql
* Sparar help.sql tills jag vet mer hur detta skapats.

Nedan är förändringar mot den scriptet tables.ascii.sql
* Splittat i två separata skript. Ett för drop table och ett för create table.
* Satt in ; i slutet av varje kommando. (Standard SQL).
* tinyint är bytt mot smallint i alla tabeller
* Microsofts (och MySQL) "datetime" & "smalldatetime" har bytts ut mot "timestamp" (Då detta är Standard SQL, vid körning av create table commandon
 byts alla ut mot datetime innan de körs. Därefter går det att arbeta på vanligt sätt med jdbc även mot SQLServer)
* I user tabellen är external bytt mot external_user (extern är ett reserverat ord i Standard SQL)
* I browsers tabellen är 'value' bytt mot 'browser_value' (value är ett reserverat ord i Standard SQL)
* I sys_data tabellen är 'value' bytt mot 'sysdata_value' (value är ett reserverat ord i Standard SQL)
* Bytte ut CAST( URRENT_TIME AS CHAR(80)) -> CAST( CURRENT_TIME AS CHAR) kodmässigt då MySQL inte stödde castning CHAR(siffror).
För att slippa göra trim på strängar i koden ändrade jag de (få) ställena med char till varchar. Detta för att MySQL trimmar alla
CHAR default, och det går inte att stänga av, så för att garanterat få samma beteende gjorde jag detta.
* CREATE TABLE lang_prefixes, lang_prefix char (3),
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


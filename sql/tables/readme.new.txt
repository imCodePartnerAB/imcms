Förändringar mot SQL Server orginalscriptet tables.ascii.sql
 * tinyint är bytt mot smallint i alla tabeller
 * Microsofts "datetime" & "smalldatetime" har bytts ut mot "timestamp"
 * I user tabellen är external bytt mot external_user.
 * I browsers tabellen är 'value' bytt mot 'browser_value'
 * I sys_data tabellen är 'value' bytt mot 'sysdata_value'
 * Microsofts text & ntext (unicode variant av text) utbytt mot VARCHAR(8000), vad betyder det för text tabellen?
   8000 är den maximala storleken SQLServer verkar stödja. Vad SQL92 säger vet jag (ännu) inte.

* MySQL krävde ytterligare förändringar:
* CREATE TABLE meta, meta_text varchar (1000) -> varchar (255)
* CREATE TABLE user_flags, description varchar (256) - varchar (255)
* CREATE TABLE frameset_docs, varchar(8000) -> varchar (255)
* CREATE TABLE texts, text varchar(8000) -> varchar (255)
* MySQL använder samma datum/tid typer som SQL Server
* Bytte ut CAST( CURRENT_TIME AS CHAR(80)) -> CAST( CURRENT_TIME AS CHAR) kodmässigt.

 * Default värden satta till NULL är borttagna
 * Andra default värden är inte satta (ännu, går det, finns det en standard?)
 * Microsofts Indexeringen är droppad, skapa annan? Verkar ingå i standardsql

 * The following feature outside Core SQL-99 is used: F391, "Long identifiers"

Förändringar mot types.sql
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

Sparar help.sql tills jag vet mer hur detta skapats.

Nästa steg
- Validera create och drop scripten mot MySQL när jag ändå håller på.
- Börja med fyra sproc:ar som gör select, delete, update och insert respektive.
- Gör förändringar på befintlig databas först
    - Flytta samtliga sproc till ett ställe och byt ut, innan migrationen
    - Flytta all SQL till ett och samma ställe

Efter varje förändring av create.sql och drop.sql se till att validera innehållet mot SQL 99:
http://developer.mimer.com/validator/parser99/index.tml

Mimer
Kör ett skript med kommandot CREATE DATABANK innan create.sql skriptet körs.
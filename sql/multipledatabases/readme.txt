Skripten i denna katalog körs av klassen DatabaseService när man anropar metoderna
initDatabase() respektive createTestData();

Se klassen DatabaseService för att se vilka databaser som stödjs.
(I skrivandets stund är det SQLServer, Mimer och MySQL)

Vid förändring av skripen i denna katalog, se till att validera innehållet mot SQL 92/99 i möjligaste mån,
http://developer.mimer.com/validator/parser92/index.tml
http://developer.mimer.com/validator/parser92/index.tml
Se även till att alla tester genom testklassen TestDatabaseService även fortsättningsvis gå att köra.

För att kunna skapa DatabaseService objectet behöver man en tom databas.
Se respektive databasleverantörs instruktioner för hur man gör detta.
Mimer: Kör ett skript med kommandot CREATE DATABANK innan create.sql skriptet körs. Se mimer.sql.
Räcker med att köra detta en gång när databasen är nyskapat.

Nedan listat förändringar som är gjorda jämfört med den tidigare SQL Server databasen.

Kvar att undersöka/göra
* Default värden satta till NULL är borttagna
* Andra default värden är inte satta (ännu, går det, finns det en standard?)
* Indexeringen är droppad så länge men borde gå att lägga till.
* Kvar är help.sql tills jag vet mer hur dessa skapats.
* lägga till en forreign key mellan owner id och user tabellen?
* Ska det finnas kvar en counter i texts?

Sproc:ar kvar att flytta in i koden:
(Ta bort den du håller på med)
classification_fix.prc
deletenewdocpermissionsetex.prc
deleteuseradminpermissibleroles.prc
existingdocsgetselectedmetaids.prc
getcategoryusers.prc
getdoctypeswithnewpermissions.prc
getdoctypeswithpermissions.prc
getmenudocchilds.prc
getmetapathinfo.prc
getpermissionset.prc
getreadrunneruserdataforuser.prc
getrolesdocpermissions.prc
gettemplategroups.prc
gettemplategroupsforuser.prc
gettemplategroupswithnewpermissions.prc
gettemplategroupswithpermissions.prc
gettemplates.prc
gettextdocdata.prc
gettexts.prc
getuseradminpermissibleroles.prc
getusercreatedate.prc
getuseridfromname.prc
getusernames.prc
getuserpermissionset.prc
getuserpermissionsetex.prc
getuserrolesdocpermissions.prc
getuserswhobelongstorole.prc
getusertype.prc
getusertypes.prc
incsessioncounter.prc
inheritpermissions.prc
inserttext.prc
ipaccessadd.prc
ipaccessdelete.prc
ipaccessesgetall.prc
ipaccessupdate.prc
listconferences.prc
listdocsbydate.prc
listdocsgetinternaldoctypes.prc
listdocsgetinternaldoctypesvalue.prc
permissionsgetpermission.prc
poll_addanswer.prc
poll_addnew.prc
poll_addquestion.prc
poll_getall.prc
poll_getallanswers.prc
poll_getallquestions.prc
poll_getanswer.prc
poll_getone.prc
poll_getquestion.prc
poll_increaseansweroption.prc
poll_setanswerpoint.prc
poll_setparameter.prc
removeuserfromrole.prc
roleaddnew.prc
roleadmingetall.prc
rolecheckconferenceallowed.prc
rolecount.prc
rolecountaffectedusers.prc
roledelete.prc
roledeleteviewaffectedmetaids.prc
roledeleteviewaffectedusers.prc
rolefindname.prc
rolegetallapartfromrole.prc
rolegetconferenceallowed.prc
rolegetname.prc
rolegetpermissionsbylanguage.prc
rolegetpermissionsfromrole.prc
rolepermissionsaddnew.prc
roleupdatename.prc
roleupdatepermissions.prc
searchdocs.prc
searchdocsindex.prc
sectionadd.prc
sectionaddcrossref.prc
sectionchangeanddeletecrossref.prc
sectionchangename.prc
sectioncount.prc
sectiondelete.prc
sectiongetall.prc
sectiongetallcount.prc
sectiongetinheritid.prc
servermasterget.prc
servermasterset.prc
setdocpermissionset.prc
setdocpermissionsetex.prc
setinclude.prc
setnewdocpermissionset.prc
setnewdocpermissionsetex.prc
setreadrunneruserdataforuser.prc
setroledocpermissionsetid.prc
shop_addshoppingitemdescription.prc
shop_addshoppingitemtoorder.prc
shop_addshoppingorder.prc
shop_getdescriptionsforshoppingitem.prc
shop_getshoppingitemsfororder.prc
shop_getshoppingorderforuserbyid.prc
shop_getshoppingordersforuser.prc
sortorder_getexistingdocs.prc
startdocget.prc
startdocset.prc
systemmessageget.prc
systemmessageset.prc
unsetuserflag.prc
updatedefaulttemplates.prc
updateparentsdatemodified.prc
updatetemplatetextsandimages.prc
userprefschange.prc
webmasterget.prc
webmasterset.prc

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
* Tog bort tabellen CREATE TABLE user_rights (
	user_id INT NOT NULL ,
	meta_id INT NOT NULL ,
	permission_id SMALLINT NOT NULL ,
	PRIMARY KEY (user_id,meta_id,permission_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
då den inte används längre
* La till på tabellen roles_rights FOREIGN KEY (set_id) REFERENCES permission_sets (set_id) och ändrade typen på set_id till INT från SMALLINT

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



För att updatera en databas med hjälpsidor gör följande.
 
OBS VIKTIGT att kolla punkt 1 !!!

1. Kontrollera att det inte finns några mallar som har id 2 tom 5
   om det redan finns mallar i WEB-INF/templates/text som heter 2.html tom 5.html
   går det ej att köra detta script. 
   Lösningen ligger i att logga in i imCMS och sedan ladda ner och upp de mallar som har id 2 tom 5
   (de får då ett nytt idnummer) och du kan lägga in våra mallar 2.html tom 5.html.
   
   Detta görs genom att först köra move_templates.sql och sedan döpa om 
   de gamla filerna 2.html tom 5.html till de som resultatet av scriptet ger.
   Forsätt sedan med punkt 2.	
  	


2. Lägg upp filerna 2.html tom 5.html i WEB-INF/templates/text

3. Lägg upp mappen helpimages för svenska bilder med hela innehållet så att strukturen blir imcmsimages/se/helpimages

4. Lägg upp mappen helpimages för engelska bilder med hela innehållet så att strukturen blir imcmsimages/en/helpimages

5. Lägg upp alla svenska admin mallar där länkarna till hjälpen finns som knappar.

6. Lägg upp alla engelska admin mallar där länkarna till hjälpen finns som knappar.

7. Lägg upp de statiska html sidorna login error osv på sina platser.

8. Bygg sql filer med ant ( ant sql )

9. Kör scriptet imcms\1.3\help.sql som ligger i roten efter byggningen 
	(!! OBS bygg nytt sql med ant för att få rätt sökvägar i help.sql.)



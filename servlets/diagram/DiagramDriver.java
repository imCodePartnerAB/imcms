import java.util.*;
import java.io.*;

import imcode.external.diverse.* ;

public class DiagramDriver{
	
	

	public static void main(String args[])	{
	
	/*
	try {
		String path = imcode.external.diverse.MetaInfo.getInternalTemplateFolder() ;
		System.out.println("GetinternalFOlder ger " + path) ;
	} catch (Exception e) {
			System.out.println("Fel av MetaINfo") ;
	}

	*/
	
/*	
// Lets try to read the settingsfile	

	DiagramDriver dd = new DiagramDriver() ;
	String path = "D:/projects/diagram/janusfide/" ;
  String prefsFile = "PREFS3_625.TXT" ;
	SettingsAccessor mySetAcc = new SettingsAccessor(path + prefsFile) ;
	dd.log("Settingsfile är :" + path + prefsFile ) ;
	mySetAcc.loadSettings() ;
	dd.log("HÄr är settingsfile: " + mySetAcc.toString()) ;
	mySetAcc = null ;
	
	*/
	
	
	// Lets loop through a directory and in every file, replace a char
	// with another...
	
	DiagramDriver dd = new DiagramDriver() ;
	String lib = "D:/diagramtest/" ;
  String aFile = "" ;

	File fileObj = new File(lib) ;
	String[] allFiles = fileObj.list() ;

	for(int i = 0; i< java.lang.reflect.Array.getLength(allFiles); i++) {		
		 // For each file...
		String theFileName = (String) java.lang.reflect.Array.get(allFiles, i) ;
		File theFile = new File(lib + theFileName) ;

		
		if( theFile.isFile()) { 
			ValueAccessor file = new ValueAccessor(lib + theFileName) ;
			file.loadAndReplace(';', '|') ;
		}
	}
	
	dd.log("Klar") ;
	
	
	
	
	// Check for an metaId in the db file
	//DiagramDriver dd = new DiagramDriver() ;
/*
		Properties params = new Properties();
		params.setProperty("META_ID", "3274") ;
		HtmlGenerator aHtml = new HtmlGenerator() ; 
		String templateLib = "D:/Projects/Diagram/JanusFide/" ;
	  String tableHeader = aHtml.createTableHeader(params, templateLib) ;
    String table = aHtml.createTable(params, templateLib) ;  
    
    dd.log("Templatelib: " + templateLib) ;
   	dd.log("tableHeader:" + tableHeader) ;
	//	dd.log("table:" + table) ;
	*/
		
	/*
		System.out.println("DiagramType: " + MetaInfo.getDiagramTypeFromFileName("Test1_234.txt")) ;
		System.out.println("DiagramType: " + MetaInfo.getDiagramTypeFromFileName("NOT_FOUND.txt")) ;
	  System.out.println("DiagramType: " + MetaInfo.getDiagramTypeFromFileName("NODOUND.txt")) ;
*/
/*		
	// READ AS A TABDELIMITED STRING


		ValueAccessor myValAcc = new ValueAccessor("C:\\TEST.TXT") ;
		char replace = ';' ;
		String aStr = myValAcc.loadAsTabDelimited(replace) ;
		System.out.println("Värdeaccessor" + aStr) ;	
		
	*/	
	

// * ********* CHECK FOR AN EMPTY ROW **********
/*
	DiagramDriver dd = new DiagramDriver() ;

	Vector v1 = new Vector() ;
	v1 = dd.initializedVector(10) ;
	v1.setElementAt("cccc", 0) ;
	String s = "" ;
	s = dd.getAllAsString(v1) ;
	
	if(dd.isRowEmpty(s))
		System.out.println("Raden var tom") ;
	else
		System.out.println("Raden var INTE tom") ;
	
	//dd.log("Här är vectorn: " + v1.toString()) ;
	
	//dd.log("Här är hela vectorn: " + dd.getAllAsString(v1)) ;
	//dd.log("Här är vectorstorleken: " + v1.size()) ;

	
	*/

	// * **************** CREATE TABLE TEST
/*
	DiagramDriver dd = new DiagramDriver() ;
HtmlGenerator htmObj = new HtmlGenerator() ;
	ValueAccessor valAcc = new ValueAccessor("c:\\tabletest.txt") ;
	valAcc.load() ;
	// Ok, lets generate a table from a vector
	Vector v = valAcc.getAllValues() ;
	int rowCount = v.size() ;
	StringManager strMan = new StringManager(v.get(0).toString(), ";") ; 
	int columnCount = strMan.getTotalItems() ;
	
	System.out.println("columncount" + columnCount ) ;
	System.out.println("First row: " + v.get(0).toString()) ;	
	
	String aStr = htmObj.generateTable(valAcc.getAllValues(), rowCount, columnCount) ;
	valAcc.reset() ;
	Vector aVect = new Vector() ;
	aVect.add(aStr) ;
	valAcc.add(aVect) ;
	//valAcc.saveValues() ;
	//System.out.println("här är html:" + aStr) ;
	//System.out.println("Här kommer TABELLEN.txt" ) ;
	System.out.println(valAcc.toString()) ;

*/

/*
// * **************** TEST TMPSETTINGSACCESSOR
	DiagramDriver dd = new DiagramDriver() ;
	TmpSettingsAccessor setAcc = new TmpSettingsAccessor("c:\\tmp.txt") ;
	setAcc.loadSettings() ;
	String header = setAcc.getSetting("Header") ;
	System.out.println("header:" + header) ;
	setAcc.saveSettings() ;
	System.out.println("Klart") ;


*/


// *****************************************************
		// ************ CREATAING A TABLE FILE CONTAINING X NBR OF COLS AND ROWS
	//	DiagramDriver dd = new DiagramDriver() ;
	//	boolean bool = dd.createFile("C:\\", "test.txt") ;
	//	ValueAccessor valAcc = new ValueAccessor("c:\\test.txt") ;
	//	valAcc.createTableSize(5, 3) ;
	//	valAcc.saveValues() ;
	//	System.out.println( "Klart") ;
	
	 // System.out.println("Så här gick skapandet: " + bool) ;

	//	boolean bool1 = dd.copyFile("C:\\", "test.txt", "C:\\", "test2.txt") ;
	 // System.out.println("Så här gick kopierandet: " + bool1) ;

	// **************************************'
	/*		/*
		
		HtmlGenerator htmObj = new HtmlGenerator() ;
		ValueAccessor valAcc = new ValueAccessor("C:\\tabdata6.txt") ;
		valAcc.load() ;
		Vector v = valAcc.getAllValues() ;
		int rowCount = v.size() ;
		StringManager strMan = new StringManager(v.get(0).toString(), ";") ; 
		int columnCount = strMan.getTotalItems() ;
		
		//System.out.println("columncount" + columnCount ) ;
		//System.out.println("First row: " + v.get(0).toString()) ;	
		
		String aStr = htmObj.createTable(valAcc.getAllValues(), rowCount, columnCount) ;
		valAcc.reset() ;
		Vector aVect = new Vector() ;
		aVect.add(aStr) ;
		valAcc.add(aVect) ;
		valAcc.saveValues() ;
	//	System.out.println("här är html:" + aStr) ;
	//	System.out.println("Här kommer Test.txt" ) ;
	//	System.out.println(valAcc.toString()) ;



	// ************ DELETING A FILE
	//	DiagramDriver dd = new DiagramDriver() ;
	//	boolean bool = dd.createFile("C:\\", "test.txt") ;
	 // System.out.println("Så här gick skapandet: " + bool) ;

	//	boolean bool1 = dd.copyFile("C:\\", "test.txt", "C:\\", "test2.txt") ;
	 // System.out.println("Så här gick kopierandet: " + bool1) ;

	// **************************************'
	/*
	 Html ht = new Html() ;
		 
 	 Vector vProps = new Vector() ;
 	 Vector vValues = new Vector() ;
 	 vProps.add("1") ;
 	 vProps.add("10") ;
   vProps.add("100") ;
   vProps.add("1000") ;
   vProps.add("10000") ;
   vProps.add("100000") ;
   vProps.add("1000000") ;
  
   vValues.add("1:1") ;
 	 vValues.add("1:10") ;
   vValues.add("1:100") ;
   vValues.add("1:1000") ;
   vValues.add("1:10000") ;
   vValues.add("1:100000") ;
   vValues.add("1:1000000") ;

	 String str = ht.createHtmlCode("OPTION", "1", vProps, vValues) ;
	 //System.out.println("Detta returnerades:" + str) ;
	
		/*
		String str = "Kliogest;252,420;245,364;300,00;400,000" ;
		StringManager dia = new StringManager(str, ";") ;
		
	   

		 System.out.println("GetItem 1:" + dia.getItem(";", 1)) ;
		 System.out.println("GetItem 2:" + dia.getItem( ";", 2)) ;
		 System.out.println("GetItem 3:" + dia.getItem( ";", 3)) ;
		 System.out.println("GetItem 4:" + dia.getItem( ";", 4)) ;
		 System.out.println("GetItem 5:" + dia.getItem( ";", 5)) ;
		 System.out.println("GetItem 6:" + dia.getItem( ";", 6)) ;
	   System.out.println("Totalitems:" + dia.getTotalItems()) ;


		//System.out.println("GetItem 1:" + this.getItem(str, ";", 1)) ;
		
		*/
		/**
		function getItem(str, delim, itemNbr)
Dim arr
Dim TotalItems 
TotalItems = 0

'Response.write("Sträng: " & str & "<BR>")

  itemNbr = itemNbr - 1
  arr = Split(str, delim, -1, 1)

  For Each x In arr
    TotalItems = TotalItems + 1
  Next 

' Response.write("TotalItems: " & TotalItems & "<BR>" )
' Response.write("itemnbr: " & itemNbr & "<BR>" )

  if itemNbr >= 0 AND itemNbr < TotalItems then
    retVal = arr(itemNbr)
  else
    retVal = ""
  end if
  retVal = trim(retVal) 

'  Response.write("Getitem returnerar: " & retVal & "<BR>")
  getItem = retVal
end function
		
		/*
		
		
				
		/* HÄR ÄR SETTINGSACCESSOR KOD
		
		SettingsAccessor setAcc = new SettingsAccessor("c:\\prefs6.txt") ;
		System.out.println("Nu läser vi!") ;
		System.out.println(	setAcc.getSetting("HEADER")) ;
		setAcc.setSetting("HEADER", "Tafs") ;
		System.out.println(	setAcc.getSetting("HEADER")) ;
	 	setAcc.saveSettings() ;
	 */
		 
	 // HÄR ÄR DIAGRAMVALUE ACCESSOR
	 
	//	ValueAccessor dataSettings = new ValueAccessor("diadata6.txt") ;
	  //	System.out.println(dataSettings.toString()) ;
	//	dataSettings.saveValues() ;
	//  System.out.println("Här kommer datasettings") ;
	//	System.out.println(dataSettings.toString()) ;
	  //	System.out.println("Så här gick det:" + (settings.fileDelete())) ;
	
	} // end main

	
	/*
	public boolean isRowEmpty(String str) {
	//	StringManager aRow = new StringManager(str, ";") ;
		int nbrOfItems = aRow.getTotalItems() ;
		
		for(int i = 0; i < nbrOfItems; i++) {
			String aStr = aRow.getItem(i) ;
			System.out.println("En item är:" + aStr) ;
			if((aStr.equals("") != true) && (aStr.equals(" ") != true))
				return false ;
		}	
		return true ;
}
*/
	
	public String getItem(String str, String delim, int itemNbr){
		String retVal = "" ;
		itemNbr = itemNbr - 1 ;
		StringTokenizer st = new StringTokenizer(str, delim) ;
		int counter = 0 ;
		
	while (st.hasMoreTokens()) {
	  String tmp = st.nextToken() ;
		if( counter == itemNbr) 
      	retVal = tmp ;	
    counter = counter + 1 ;
	}
		return retVal ;
	}	
	
	
public boolean fileDelete(String thePath, String theFile) {
		boolean ok = false ;
		try {
			File fileObj = new File(thePath + theFile) ;
			if(fileObj.exists()) 
				ok = fileObj.delete() ;
			else
				log("File not found! " + thePath + theFile) ;
		
		} catch (NullPointerException e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
			}
			catch (Exception e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
			}
		
		return ok ;
}	// end filedelete
	
	
	public boolean fileExists(String thePath, String theFile) {
     return this.fileExists(thePath + theFile) ;
	}
	
	public boolean fileExists(String theFile) {
		boolean ok = false ;
		try {
			File fileObj = new File( theFile) ;
			if(fileObj.exists()) 
				ok = true ;
			else
				log("FileExists. File not found! " + theFile) ;
						
		} catch (NullPointerException e) {
					log("The file couldnt be found " + e.getMessage()) ;
			}
			catch (Exception e) {
					log("The file couldnt be found " + e.getMessage()) ;
			}
		
		return ok ;
}	// end fileExists

	

public boolean createFile(String thePath, String newFile) {
		boolean ok = false ;
		try {
			File fileObj = new File(thePath + newFile) ;
			// Lets create a new file 
			ok = fileObj.createNewFile() ;
		  if(ok != true)
		 		log("The file could not be created!" + thePath + newFile) ;
		} catch (NullPointerException e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
			}
			catch (Exception e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
			}
		
		return ok ;
}	// end filedelete


public boolean copyFile(String srcPath, String srcFile, String targPath, String targFile) {
	  boolean okFlag = false ;
	  boolean fileNotFoundFlag = false ;

		try {
			if( this.fileExists(srcPath + srcFile))
				okFlag = writeFileCopy(srcPath + srcFile, targPath + targFile) ;
	 	
	 	}	catch (FileNotFoundException exc) {
			log("Could not find the file \"" + srcPath + srcFile + "\".");
			log("Make sure it is in the current directory.") ;
			log("" + exc);
	 		return false ;
			
		}	catch (IOException exc) {
			log("IO error occurred while reading file: " + targPath + targFile);
			log("" + exc);
			return false ;
		}
			return okFlag ;
	}



 public boolean writeFileCopy(String srcFile, String targetFile)
 	throws IOException {
		
		boolean ok = false ;
		try	{
				
		// Lets open the valuefile
			log("Copying File: " + srcFile + " to " +  targetFile + "...") ;
			BufferedReader inputFromFile = new BufferedReader(new FileReader(srcFile));
			
			// create a file writer for the target file and set append to true			
		//	boolean append = true;
			FileWriter myFileWriter = new FileWriter(targetFile, false);
			
			// create a print writer based on fileWriter and set autoflush to true		
			boolean autoFlush = true;
			PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
	
			String aLine;
		  while ((aLine = inputFromFile.readLine()) != null ) {			
					outputToFile.println(aLine) ;
		  }

			inputFromFile.close();
			outputToFile.close() ;
						
		}	catch (IOException exc) {
				log("Error occurred during the save.");
				log("" + exc);
				ok = false ;
			}
		
		ok = true ;
		return ok ;
	} // loadvalues


/*


protected Vector readValues(BufferedReader inputFromFile) throws IOException {
		String aLine;
		 Vector v = new Vector() ;

		while ((aLine = inputFromFile.readLine()) != null ) {			
				v.add(aLine) ;
		}
		
		return v ;
	}

public boolean saveValues() {
		try {
			writeValues() ;
	 	
	 	}	catch (FileNotFoundException exc) {
			log("Could not find the file \"" + FILE_NAME + "\".");
			log("Make sure it is in the current directory.") ;
			log(exc);
	 		return false ;
			
		}	catch (IOException exc) {
			log("IO error occurred while reading file: " + FILE_NAME);
			log(exc);
			return false ;
		}
			return true ;
	}

	protected void writeValues() throws IOException {
		try {
			log("Doing save...");
			
		// create a file writer for the file "music.db" and set append to true			
		//	boolean append = true;
			FileWriter myFileWriter = new FileWriter(FILE_NAME, false);
			
			// create a print writer based on fileWriter and set autoflush to true		
			boolean autoFlush = true;
			PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
			String str = "" ;
		
			for(int i = 0 ; i < diagramValues.size() ; i++) {
				str = (String) diagramValues.get(i) ;
     	 	outputToFile.println(str) ;		
			}

			outputToFile.close() ;
			
		}	catch (IOException exc) {
			log("Error occurred during the save.");
			log(exc);
		}
		
	} 
	

*/



public void log(String msg){
	System.out.println("DiagramDriver:" + msg ) ;	
}

private Vector initializedVector(int size) {
	Vector vect = new Vector(size) ;
	for(int i = 0; i < size; i++) {
		vect.insertElementAt(" ;", i ) ;
	}
	return vect ;	
}

private String getAllAsString(Vector v){
	String s = "" ;
	for(int i = 0; i < v.size(); i++) {
		s += v.get(i) ;
	}
	return s;	
}



} // end DiagramDriver
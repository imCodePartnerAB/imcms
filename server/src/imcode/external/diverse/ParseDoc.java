package imcode.external.diverse ;

import java.io.* ;
import java.util.*;

public class ParseDoc {
    String m_FileData = "";
    File m_FileName ;
    
    
    ParseDoc(File fileName) {
        m_FileName = fileName ;
    }
    
    String parse(Vector tags,Vector data) {
        
        int findPos ;
        for(int i = 0 ; i < tags.size() ;i++)
            if ((findPos = m_FileData.indexOf(tags.elementAt(i).toString())) != -1)
                m_FileData =this.replaceTag(tags.elementAt(i).toString(), findPos, m_FileData, data.elementAt(i).toString()) ;
        
        return m_FileData ;
    }
    
    void readFile() {
        try {
            String fileLine ;
            String tempStr ;
            // Get the  file specified by InputFile
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(m_FileName)));
            //while there are still lines in the file, get-em.
            while((fileLine = br.readLine())!= null)			{
                //add each line to the vector, each line will have a CRLF
                tempStr = fileLine.trim() + "\n" ;
                if (tempStr.length() > 0)
                    m_FileData += tempStr ;
            }
            
            //IMPORTANT!!!! - CLOSE THE STREAM!!!!!
            br.close();
        }		catch(IOException e)		{
            System.out.println("An error occurred reading the file" + e + "\n");
        }
        
    }
    
    String replaceTag (String tag, int tagStart, String str, String insertStr) {
        
        String temp_str1 = str.substring (0, tagStart);
        String temp_str2 = str.substring (tagStart+(tag.length ()), str.length ());
        str  = temp_str1 ;
        str += insertStr;
        str += temp_str2 ;
        
        return str;
    }
    
}


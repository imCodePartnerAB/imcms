package imcode.util ;

import java.util.*;
import java.io.*;
import java.io.FilenameFilter;


public class GetImages implements FilenameFilter, Comparator {

    private static GetImages _this = new GetImages() ;
    
    /**
	Private constructor, to prevent unnecessary instantiation.
    */
    private GetImages () {
    
    }

    /**
    	* puts all files in the suplied directory (filtered) in ArrayList
    	@param path the folder i.e. "D:\\Apache\\htdocs\\images"
    	@param sorted ArrayList sorted
    **/

    static public List getImageFilesInFolder(File path, boolean sorted) 
	    {
		List arrlist = new ArrayList();
		File[] _imgArr = path.listFiles(_this);
		

		for(int i=0;i<_imgArr.length;i++) 
		{
			if( !_imgArr[i].isDirectory() )
				arrlist.add(_imgArr[i]);
		}

		// sorts arrlist using compare-method of MenuItemComparator
		if(sorted) Collections.sort(arrlist,_this);
		return arrlist ;
	}
	
	/**
    	* reads down the file-hierarchy and puts all folders (filtered) in ArrayList
    	@param path the filepath i.e. "D:\\Apache\\htdocs\\images"
    	@param sorted ArrayList sorted
    **/

    static public List getImageFolders(File path, boolean sorted) 
	{
		List arrlist = new ArrayList();
		File[] _imgArr = path.listFiles(_this);
		

		for(int i=0;i<_imgArr.length;i++) 
		{
			if (_imgArr[i].isDirectory())
			{
				arrlist.add(_imgArr[i]);
				arrlist.addAll(getImageFolders(_imgArr[i], true));
			}
		}

		// sorts arrlist using compare-method of MenuItemComparator
		if(sorted) Collections.sort(arrlist,_this);
		return arrlist ;
	}
	
  	

/**
	* implemented FilenameFilter-method
	@param file
	@param string
**/
	public boolean accept(File file, String string) {
		//String path = new String(file.toString() + File.separatorChar + string);
		boolean b_1 = new File(file,string).isDirectory();
		string = string.toLowerCase() ;
		boolean b_2 = string.endsWith(".jpg");
		boolean b_3 = string.endsWith(".jpeg");
		boolean b_4 = string.endsWith(".gif");
		boolean b_5 = string.endsWith(".png");
		return (b_1 || b_2 || b_3 || b_4 || b_5);
	}
	
/**
	* implemented MenuItemComparator-method, compares the filenames, regardless of path
	@param o1
	@param o2
**/
	public int compare(Object o1, Object o2) 
	{
		String s1 = ((File)o1).getPath();
		String s2 = ((File)o2).getPath();
		return s1.compareTo(s2);
	}
}


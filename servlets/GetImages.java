import java.util.*;
import java.io.*;
import java.io.FilenameFilter;


class GetImages implements FilenameFilter, Comparator
{

/**
	* Recurses :) down the file-hierarchy and puts all files (filtered) in ArrayList
	@param path the filepath i.e. "D:\\Apache\\htdocs\\images"
	@param fullPath file stored with full path
	@param sorted ArrayList sorted
**/
public List getImageFiles(String path, boolean fullPath, boolean sorted) 
	{
	
		// fullpath not used, and will not be used, just remains as
		// a relic... a reminder of good intentions.
		String FILE_SEP = System.getProperty("file.separator");
		List arrlist = new ArrayList();
		File _file = new File(path);
		File _thisFile;
		String[] _imgArr = _file.list(this);

		for(int i=0;i<_imgArr.length;i++) 
		{
			_thisFile = new File(path + FILE_SEP + _imgArr[i]);
			if(_thisFile.isDirectory())
				arrlist.addAll(getImageFiles(path + FILE_SEP + _imgArr[i], true, true));
			else
				arrlist.add(_thisFile);
		}

		// sorts arrlist using compare-method of Comparator
		if(sorted) Collections.sort(arrlist,this);
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
		boolean b_2 = string.endsWith(".jpg");
		boolean b_3 = string.endsWith(".jpeg");
		boolean b_4 = string.endsWith(".gif");
		boolean b_5 = string.endsWith(".png");
		return (b_1 || b_2 || b_3 || b_4 || b_5);
	}
	
/**
	* implemented Comparator-method, jämför filnamnen, oavsett path
	@param o1
	@param o2
**/
	public int compare(Object o1, Object o2) 
	{
		String s1 = ((File)o1).getName();
		String s2 = ((File)o2).getName();
		return s1.compareTo(s2);
	}


}


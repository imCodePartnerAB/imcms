package imcode.util ;
import imcode.util.log.*;
import java.util.*;
import java.io.*;
import java.io.FilenameFilter;


public class ImageFileMetaData
{
	protected Log log = Log.getLog("server");
	protected int width, height;		// the width and the height of the image
	protected File file;				// imageFile fileObject
	protected String type;				// the type of the image i.e. GIF87a, GIF89a, JFIF

	public ImageFileMetaData(File _file)
	{
		file = _file;
		width = 0;
		height = 0;
		type = "";

		try
		{
			if(!file.exists())
				throw(new FileNotFoundException(file.getName()));
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			if (file.getName().endsWith(".gif")) // ****************** A GIF-File
				{
					for(int slask=0;slask<6;slask++)
						type += (char)(dis.read());
					if(!type.equals("GIF87a") && !type.equals("GIF89a"))
						throw(new Exception());		
					width = (dis.read() + (dis.read()<<8));
					height = (dis.read() + (dis.read()<<8));
					log.log(Log.INFO, "width: " + width);
					log.log(Log.INFO, "height: " + height);
					dis.close();
				}
		else if (file.getName().endsWith(".jpg")) // ***************** A JPG-File
		{
			boolean doLoop = true; // loop-flag
			boolean panic = false; // loop-flag, panic-state
			int panicCounter = 0;
			dis.skipBytes(2); // skip the header-info
			while (doLoop && !panic)
			{
				panicCounter++;
				if(panicCounter > 10)
					panic = true; // panic out of this loop, something's wrong with this file!!!
				int startOfBlock = dis.read(); // should be FF (255)
				int blockType = dis.read(); // wish for  C0-3 (192,193,194)
				int lengthOfBlock = (dis.read()<<8) + dis.read();
				if (blockType==192 || blockType==193 || blockType==194) // in case of a C0-C3-block
				{
					dis.read(); // dataPrecision byte of no interest
					height = (dis.read()<<8) + dis.read();
				 	width = (dis.read()<<8) + dis.read();
					log.log(Log.INFO, "width: " + width);
					log.log(Log.INFO, "height: " + height);
					doLoop = false; // skip the loop
				}
				else // if NOT a C0-3-block, skip this block
					dis.skipBytes(lengthOfBlock -2);
				} 
				dis.close();
			}
			
		else if (file.getName().endsWith(".png")) // ***************** A PNG-File
		{
			// PNG starts with 8 bytes (here in decimal): 137 80 78 71 13 10 26 10
			//  indicating this is a PNG-file
			// Then blocks in this format:
			// 		Length: 4 bytes (not including itself, chunkType or CRC)
			// 		ChunkType: 4 bytes
			// 		ChunkData: ?
			// 		CRC: 4 bytes
			
			// The first chunk is the IHDR (ImageHeader Chunk) containing:
			// Width				4 bytes
			// Height				4 bytes
			// Bit depth			1 byte
			// Color type			1 byte
			// Compression method	1 byte
			// Filter method		1 byte
			// Interlace method		1 byte
			
			for(int slask=0;slask<8;slask++)
						type += "" + dis.read() + " ";
			if(type.equals("137 80 78 71 13 10 26 10 "))
			{
				dis.skipBytes(4); // skip the blockLength
				String header = "";
				for(int slask=0;slask<4;slask++)
						header += (char)(dis.read());
				if (header.equals("IHDR"))
				{
					width = (dis.read()<<32) + (dis.read()<<16) + (dis.read()<<8) + dis.read();
					height = (dis.read()<<32) + (dis.read()<<16) + (dis.read()<<8) + dis.read();
					dis.close();
				}		
			}		
		
		}
		
		else // unknown file-suffix
			{ log.log(Log.ERROR, "Ogiltigt suffix"); }
		}
		catch(FileNotFoundException e) { log.log(Log.ERROR, "Filnamnsfel!, " + e); }
		catch(EOFException e) { log.log(Log.ERROR, "Filformatfel: blockgräns pekar utanför filen!, " + e); }
		catch(IOException e) { log.log(Log.ERROR, "Filläsningsfel!, " + e); }
		catch(Exception e) { log.log(Log.ERROR, "Fel!, " + e); }
		

	}
	
	/**
	* getWidth
	* @author M Wallin
	* @version 0.2
	* @param none
	* @return imagewidth
	**/
	public int getWidth()
	{
		return width;
	}
	/**
	* getHeight
	* @author M Wallin
	* @version 0.2
	* @param none
	* @return imageheight
	**/	
	public int getHeight()
	{	
		return height;
	}
	/**
	* getName
	* @author M Wallin
	* @version 0.2
	* @param none
	* @return filename
	**/	
	public String getName()
	{
		return file.getName();
	}
	/**
	* getType
	* @author M Wallin
	* @version 0.2
	* @param none
	* @return imagetype (returns GIF87a or GIF89a for GIF-files, nothing yet for JPG-files)
	**/	
	public String getType()
	{
		return type;
	}

/*
		TODO:
		String getImageInfo() -> artist and copyright-info		JPG/GIF
		boolean isInterlaced()									GIF
		boolean isAnimated()									GIF
		int countFrames()										GIF
		int paletteSize()										GIF
		int colorDepth()										JPG/GIF
		
		
		
*/

}


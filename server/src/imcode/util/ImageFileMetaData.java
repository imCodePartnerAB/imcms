package imcode.util ;
import imcode.util.log.*;
import java.util.*;
import java.io.*;
import java.io.FilenameFilter;


public class ImageFileMetaData
{

    static final byte[] PNG_HEADER = { -119, 80, 78, 71, 13, 10, 26, 10 } ;

    public static final int MARKER = 0xFF ; // Start of segment marker

    public static final int SOI  = 0xD8 ; // Start of Image

    public static final int SOF0 = 0xC0 ; // Start of frame 0
    public static final int SOF1 = 0xC1 ; // Start of frame 1
    public static final int SOF2 = 0xC2 ; // Start of frame 2
    public static final int SOF3 = 0xC3 ; // Start of frame 3

    public static final int APP0 = 0xE0 ; // JFIF Segment Marker
    public static final int SOS  = 0xDA ; // Start of Scan

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
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		if (file.getName().endsWith(".gif")) // ****************** A GIF-File
		    {
			byte[] t = new byte[6] ;
			dis.readFully(t);
			type = new String(t,"8859_1") ;
			if(!type.equals("GIF87a") && !type.equals("GIF89a")) {
			    throw(new UnsupportedOperationException("Unsupported gif-type"));
			}
			width = (dis.read() + (dis.read()<<8));
			height = (dis.read() + (dis.read()<<8));
			//log.log(Log.INFO, "width: " + width);
			//log.log(Log.INFO, "height: " + height);
			dis.close();
		    }

		else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) { // FIXME: Move into a separate (protected?) method. Preferably one that takes just a File
		    if(dis.read() != MARKER || dis.read() != SOI) {
			throw(new UnsupportedOperationException("Unsupported jpeg-type"));
		    }
		loop: 
		    for ( ;; ) {
			while (dis.read()!=MARKER) ; // Skip until we get 0xFF (Next marker)
			int blockType ;
			while ((blockType = dis.read()) == MARKER) ; // Skip until we get something not 0xFF (the segmenttype), and wish for C0 to C3
			int lengthOfBlock = (dis.read()<<8) + dis.read(); // Get length of the segment
			switch (blockType) {

			case SOF0 : // SOF0 Start of frame 0
			case SOF1 :
			case SOF2 :
			case SOF3 :
			    int precision = dis.read() ;
			    if ( precision != 0x08) {
				log.log(Log.WARNING, "Trying to read jpeg with unsupported precision (0x"+Integer.toHexString(precision)+")",file) ;
			    }
			    height = (dis.read()<<8) + dis.read() ;
			    width = (dis.read()<<8) + dis.read() ;
			    //log.log(Log.DEBUG, "width: " + width);
			    //log.log(Log.DEBUG, "height: " + height);
			    dis.skipBytes(lengthOfBlock-7);
			    break ;

			case APP0 : // APP0 JFIF segment marker
			    byte[] t = new byte[5] ;
			    dis.readFully(t);
			    type = new String(t,"8859_1") ;
			    if(!type.equals("JFIF\0")) {
				log.log(Log.WARNING, "Trying to read jpeg with unsupported type ("+type+")",file) ;
			    }
			    dis.skipBytes(lengthOfBlock-7);
			    break ;

			case SOS : // SOS Start of Scan
			    break loop ;

			default:
			    if (lengthOfBlock > 2) {
				dis.skipBytes(lengthOfBlock-2);
			    }
			}
		    } 
		}
		/*
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
		  }*/
			
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
			
			byte[] t = new byte[8] ;
			dis.readFully(t);

			//for(int slask=0;slask<8;slask++)
			//    type += "" + dis.read() + " ";
			//if(type.equals("137 80 78 71 13 10 26 10 "))
			if (t.equals( PNG_HEADER ))
			    {
				dis.skipBytes(4); // skip the blockLength
				byte[] u = new byte[4] ;
				dis.readFully(u) ;
				String header = new String(u,"8859_1") ;
				if (header.equals("IHDR"))
				    {
					width = (dis.read()<<32) + (dis.read()<<16) + (dis.read()<<8) + dis.read();
					height = (dis.read()<<32) + (dis.read()<<16) + (dis.read()<<8) + dis.read();
					dis.close();
				    }		
			    }		
		
		    }
		
		else {
		    log.log(Log.ERROR, "Unknown suffix");
		}
		dis.close() ;
	    } catch(RuntimeException e) {
		log.log(Log.ERROR, "Getting size from image", e);
	    } catch(IOException e) {
		log.log(Log.ERROR, "Getting size from image", e);
	    }

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


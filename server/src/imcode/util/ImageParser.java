package imcode.util;

import org.apache.log4j.Logger;

import java.io.*;

public class ImageParser {

    /**
     * PNG Header Byte Sequence 0x *
     */
    private static final byte[] PNG_HEADER = {-119, 80, 78, 71, 13, 10, 26, 10};

    /**
     * JPEG Start of segment marker *
     */
    private static final int MARKER = 0xFF;

    /**
     * JPEG Start of Image *
     */
    private static final int SOI = 0xD8;

    /**
     * JPEG Start of frame 0 *
     */
    private static final int SOF0 = 0xC0;

    /**
     * JPEG Start of frame 1 *
     */
    private static final int SOF1 = 0xC1;

    /**
     * JPEG Start of frame 2 *
     */
    private static final int SOF2 = 0xC2;

    /**
     * JPEG Start of frame 3 *
     */
    private static final int SOF3 = 0xC3;

    /**
     * JPEG JFIF Segment Marker *
     */
    private static final int APP0 = 0xE0;

    /**
     * JPEG Start of Scan *
     */
    private static final int SOS = 0xDA;

    private final static Logger log = Logger.getLogger( "imcode.util.ImageFileMetaData" );

    public ImageData parseImageFile( File imageFile ) throws IOException {
        return parseImageStream( new FileInputStream( imageFile ), imageFile.getName() );
    }

    public ImageData parseImageStream( InputStream imageStream, String fileName ) throws IOException, IllegalArgumentException {
        if ( fileName.toLowerCase().endsWith( ".gif" ) ) {
            return parseGifStream( imageStream );
        } else if ( fileName.toLowerCase().endsWith( ".jpg" ) || fileName.toLowerCase().endsWith( ".jpeg" ) ) { // FIXME: Move into a separate (protected?) method. Preferably one that takes just a File
            return parseJpegStream( imageStream, fileName );
        } else if ( fileName.toLowerCase().endsWith( ".png" ) ) {
            return parsePngStream( imageStream );
        } else {
            throw new IllegalArgumentException( "Unrecognized image-filename: " + fileName );
        }
    }

    private ImageData parsePngStream( InputStream imageStream ) throws IOException {
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
        DataInputStream dis = new DataInputStream( new BufferedInputStream( imageStream ) );

        try {
            byte[] t = new byte[8];
            dis.readFully( t );

            int width = 0;
            int height = 0;
            String type = "PNG";
            if ( t.equals( PNG_HEADER ) ) {
                dis.skipBytes( 4 ); // skip the blockLength
                byte[] u = new byte[4];
                dis.readFully( u );
                String header = new String( u, "8859_1" );
                if ( header.equals( "IHDR" ) ) {
                    // FIXME: There should be no <<32 below.
                    width = ( dis.read() << 24 ) | ( dis.read() << 16 ) | ( dis.read() << 8 ) | dis.read();
                    height = ( dis.read() << 24 ) | ( dis.read() << 16 ) | ( dis.read() << 8 ) | dis.read();
                }
            }
            return new ImageData( width, height );
        } finally {
            dis.close();
        }
    }

    private ImageData parseJpegStream( InputStream imageStream, String fileName ) throws IOException {
        DataInputStream dis = new DataInputStream( new BufferedInputStream( imageStream ) );
        try {
            if ( dis.read() != MARKER || dis.read() != SOI ) {
                throw( new UnsupportedOperationException( "Unsupported jpeg-type" ) );
            }
            int width = 0;
            int height = 0;
            loop:
            for ( ; ; ) {
                skipUntilMarker( dis );
                int blockType = skipMarkersUntilBlockType( dis );
                int lengthOfBlock = readJpegInt( dis ); // Get length of the segment
                switch ( blockType ) {
                    case SOF0: // SOF0 Start of frame 0
                    case SOF1: // SOF1 Start of frame 1
                    case SOF2: // SOF2 Start of frame 2
                    case SOF3: // SOF3 Start of frame 3
                        int precision = dis.read();
                        if ( precision != 0x08 ) {
                            log.warn( "Trying to read jpeg with unsupported precision (0x"
                                      + Integer.toHexString( precision )
                                      + ")"
                                      + fileName );
                        }
                        height = readJpegInt( dis );
                        width = readJpegInt( dis );
                        skipRestOfStartOfFrameBlock( dis, lengthOfBlock );
                        break;

                    case APP0: // APP0 JFIF segment marker
                        String type = readTypeFromAPP0Block( dis );
                        if ( !type.equals( "JFIF\0" ) ) {
                            log.warn( "Trying to read jpeg with unsupported type (" + type + ")" + fileName );
                        }
                        skipRestOfAPP0Block( dis, lengthOfBlock );
                        break;

                    case SOS: // SOS Start of Scan
                        break loop;

                    default:
                        if ( lengthOfBlock > 2 ) {
                            dis.skipBytes( lengthOfBlock - 2 );
                        }
                }
            }
            return new ImageData( width, height );
        } finally {
            dis.close();
        }
    }

    private String readTypeFromAPP0Block( DataInputStream dis ) throws IOException {
        String type;
        byte[] t = new byte[5];
        dis.readFully( t );
        type = new String( t, "8859_1" );
        return type;
    }

    private void skipRestOfAPP0Block( DataInputStream dis, int lengthOfBlock ) throws IOException {
        dis.skipBytes( lengthOfBlock - 7 );
    }

    private void skipRestOfStartOfFrameBlock( DataInputStream dis, int lengthOfBlock ) throws IOException {
        dis.skipBytes( lengthOfBlock - 7 );
    }

    private int readJpegInt( DataInputStream dis ) throws IOException {
        return ( dis.read() << 8 ) + dis.read();
    }

    private int skipMarkersUntilBlockType( DataInputStream dis ) throws IOException {
        int blockType;
        while ( ( blockType = dis.read() ) == MARKER ) {
            ; // Skip until we get something not 0xFF (the segmenttype), and wish for C0 to C3
        }
        return blockType;
    }

    private void skipUntilMarker( DataInputStream dis ) throws IOException {
        while ( dis.read() != MARKER ) {
            ; // Skip until we get 0xFF (Next marker)
        }
    }

    private ImageData parseGifStream( InputStream imageStream ) throws IOException {
        DataInputStream dis = new DataInputStream( new BufferedInputStream( imageStream ) );
        try {
            byte[] t = new byte[6];
            dis.readFully( t );
            String type = new String( t, "8859_1" );
            if ( !type.equals( "GIF87a" ) && !type.equals( "GIF89a" ) ) {
                throw( new UnsupportedOperationException( "Unsupported gif-type" ) );
            }
            int width = ( dis.read() + ( dis.read() << 8 ) );
            int height = ( dis.read() + ( dis.read() << 8 ) );
            return new ImageData( width, height );
        } finally {
            dis.close();
        }
    }

}


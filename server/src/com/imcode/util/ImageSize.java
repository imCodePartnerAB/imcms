package com.imcode.util;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

public class ImageSize implements Serializable {

    private int width;
    private int height;

    public ImageSize( int width, int height ) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width ;
    }

    public int getHeight() {
        return height ;
    }

    public String toString() {
        return width+"*"+height ;
    }

    public static ImageSize fromInputStream( InputStream inputStream ) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream( inputStream );
        Iterator imageReadersIterator = ImageIO.getImageReaders( imageInputStream );
        if ( !imageReadersIterator.hasNext() ) {
            throw new IOException( "Can't read image format." ) ;
        }
        ImageReader imageReader = (ImageReader)imageReadersIterator.next();
        imageReader.setInput( imageInputStream, true, true );
        int width = imageReader.getWidth( 0 );
        int height = imageReader.getHeight( 0 );
        imageReader.dispose();
        return new ImageSize( width, height );
    }
}
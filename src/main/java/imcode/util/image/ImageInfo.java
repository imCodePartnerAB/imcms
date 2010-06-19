package imcode.util.image;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    private static final long serialVersionUID = 8901634883271727973L;
    
    private Format format;
    private int width;
    private int height;

    
    public ImageInfo() {
    }

    public ImageInfo(Format format) {
        this.format = format;
    }

    public ImageInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ImageInfo(Format format, int width, int height) {
        this.format = format;
        this.width = width;
        this.height = height;
    }

    
    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

package imcode.util;

public class ImageSize {

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
}
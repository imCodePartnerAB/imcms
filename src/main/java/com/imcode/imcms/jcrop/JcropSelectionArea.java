package com.imcode.imcms.jcrop;

import java.io.Serializable;

/**
 * A Jcrop selection area that is given by two corner points: top left {@code (x1, y1)}
 * and bottom right {@code (x2, y2)}.
 */
public class JcropSelectionArea implements Serializable {
    private int x1;
    private int y1;
    private int x2;
    private int y2;


    /**
     * All coordinates are set to {@code 0} and this selection is not valid.
     */
    public JcropSelectionArea() {
    }

    /**
     * @param x1 the top left x-coordinate
     * @param y1 the top left y-coordinate
     * @param x2 the bottom right x-coordinate
     * @param y2 the bottom right y-coordinate
     */
    public JcropSelectionArea(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Test if the coordinates specified by this selection area form a valid selection.
     *
     * @return  true if this selection has an area (width * height) that is greater than 0, otherwise false
     */
    public boolean isValid() {
        int area = getWidth() * getHeight();

        return area > 0;
    }

    /**
     * @return  the width of this selection
     */
    public int getWidth() {
        return Math.max(x1, x2) - Math.min(x1, x2);
    }

    /**
     * @return  the height of this selection
     */
    public int getHeight() {
        return Math.max(y1, y2) - Math.min(y1, y2);
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }
}

package imcode.util.image;

public enum Resize {
    /** Always resize by width x height, preserves aspect ratio. One of the dimensions can be optional. */
    DEFAULT("", 1), 
    /** Always resize by width x height, ignores aspect ratio. */
    FORCE("!", 2), 
    /** Resize by width x height, only if both of the dimensions are larger than requested, preserves aspect ratio. */
    LESS_THAN("<", 3), 
    /** Resize by width x height, only if one of the dimensions is larger than requested, preserves aspect ratio. */
    GREATER_THAN(">", 4), 
    /** 
     * Dimensions scaled by percentage. If only width is specified, then both width and height will be scaled. 
     * Otherwise each dimension will be scaled by a individual percentage.
     */
    PERCENT("%", 5);
    
    private final String modifier;
    private final int ordinal;
    
    private Resize(String modifier, int ordinal) {
        this.modifier = modifier;
        this.ordinal = ordinal;
    }

    public String getModifier() {
        return modifier;
    }

    public int getOrdinal() {
        return ordinal;
    }
    
    public static Resize getByName(String value) {
        if (value == null) {
            return null;
        }
        
        value = value.toLowerCase();
        
        for (Resize resize : Resize.values()) {
            if (value.equals(resize.name().toLowerCase())) {
                return resize;
            }
        }
        
        return null;
    }
    
    public static Resize getByOrdinal(int ord) {
        for (Resize resize : Resize.values()) {
            if (resize.getOrdinal() == ord) {
                return resize;
            }
        }
        
        return null;
    }
}

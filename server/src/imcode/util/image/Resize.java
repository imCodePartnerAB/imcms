package imcode.util.image;

public enum Resize {
    DEFAULT(""), 
    FORCE("!"), 
    LESS_THAN("<"), 
    GREATER_THAN(">"), 
    PERCENT("%");
    
    private final String modifier;
    
    private Resize(String modifier) {
        this.modifier = modifier;
    }

    public String getModifier() {
        return modifier;
    }
}

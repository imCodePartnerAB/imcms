package imcode.util.image;

public enum Gravity {
    NORTH_WEST("NorthWest"), 
    NORTH("North"), 
    NORTH_EAST("NorthEast"), 
    WEST("West"), 
    CENTER("Center"), 
    EAST("East"), 
    SOUTH_WEST("SouthWest"), 
    SOUTH("South"), 
    SOUTH_EAST("SouthEast");

    private final String gravity;

    private Gravity(String gravity) {
        this.gravity = gravity;
    }

    public String getGravity() {
        return gravity;
    }
}

package imcode.util.image;

public enum Color {
    WHITE("white"),
    BLACK("black"),
    TRANSPARENT("transparent");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
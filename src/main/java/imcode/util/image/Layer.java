package imcode.util.image;

public enum Layer {
    COMPARE_ANY("compare-any"),
    COMPARE_CLEAR("compare-clear"),
    COMPARE_OVERLAY("compare-overlay"),
    COALESCE("coalesce"),
    COMPOSITE("composite"),
    DISPOSE("dispose"),
    FLATTEN("flatten"),
    MERGE("merge"),
    MOSAIC("mosaic"),
    OPTIMIZE("optimize"),
    OPTIMIZE_FRAME("optimize-frame"),
    OPTIMIZE_PLUS("optimize-plus"),
    OPTIMIZE_TRANSPARENCY("optimize-transparency"),
    REMOVE_DUPS("remove-dups"),
    REMOVE_ZERO("remove-zero"),
    TRIM_BOUNDS("trim-bounds");

    private final String layer;

    Layer(String layer) {
        this.layer = layer;
    }

    public String getLayer() {
        return layer;
    }
}

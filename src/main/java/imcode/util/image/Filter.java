package imcode.util.image;

public enum Filter {
    POINT("Point"), 
    HERMITE("Hermite"), 
    CUBIC("Cubic"), 
    BOX("Box"), 
    GAUSSIAN("Gaussian"), 
    CATROM("Catrom"), 
    TRIANGLE("Triangle"), 
    QUADRATIC("Quadratic"), 
    MITCHELL("Mitchell"), 

    LANCZOS("Lanczos"), 
    HAMMING("Hamming"), 
    PARZEN("Parzen"), 
    BLACKMAN("Blackman"), 
    KAISER("Kaiser"), 
    WELSH("Welsh"), 
    HANNING("Hanning"), 
    BARTLET("Bartlet"), 
    BOHMAN("Bohman");

    private final String filter;

    private Filter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }
}
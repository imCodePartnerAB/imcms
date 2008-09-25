package imcode.util;

import org.apache.commons.collections.Factory;

public class CounterStringFactory implements Factory {

    int counter ;

    public CounterStringFactory() {
        this(0) ;
    }

    public CounterStringFactory( int counterStartValue ) {
        counter = counterStartValue ;
    }

    public Object create() {
        return "" + counter++;
    }
}

package imcode.util;

import org.apache.commons.collections.Transformer;

public class ToStringPairArrayTransformer implements Transformer {

    public Object transform( Object input ) {
        return new String[] {""+input,""+input} ;
    }
}

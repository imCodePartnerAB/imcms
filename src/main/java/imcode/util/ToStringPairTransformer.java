package imcode.util;

import org.apache.commons.collections.Transformer;

public abstract class ToStringPairTransformer implements Transformer {
    public final Object transform(Object object) {
        return transformToStringPair(object);
    }

    protected abstract String[] transformToStringPair(Object object) ;
}

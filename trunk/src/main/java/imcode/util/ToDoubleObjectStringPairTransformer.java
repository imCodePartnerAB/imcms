package imcode.util;

public class ToDoubleObjectStringPairTransformer extends ToStringPairTransformer {

    protected String[] transformToStringPair(Object object) {
        return new String[] {""+object,""+object} ;
    }

}

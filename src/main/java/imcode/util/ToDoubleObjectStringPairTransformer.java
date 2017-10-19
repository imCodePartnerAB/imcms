package imcode.util;

import java.util.function.Function;

public class ToDoubleObjectStringPairTransformer implements Function<Integer, String[]> {

    public String[] apply(Integer number) {
        return new String[]{String.valueOf(number), String.valueOf(number)};
    }

}

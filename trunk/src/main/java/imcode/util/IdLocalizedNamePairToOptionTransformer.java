package imcode.util;

import org.apache.commons.collections.Transformer;

public class IdLocalizedNamePairToOptionTransformer implements Transformer {

    private String language;

    public IdLocalizedNamePairToOptionTransformer(String language) {
        this.language = language ;
    }

    public Object transform(Object input) {
        IdLocalizedNamePair pair = (IdLocalizedNamePair) input ;
        return new String[] {pair.getId()+"", pair.getName().toLocalizedString(language)};
    }
}

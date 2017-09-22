package com.imcode.imcms.mapping.mappers;

public interface Mappable<FROM, TO> {

    TO map(FROM source);

}

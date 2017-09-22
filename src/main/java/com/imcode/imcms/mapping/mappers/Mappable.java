package com.imcode.imcms.mapping.mappers;

@FunctionalInterface
public interface Mappable<FROM, TO> {

    TO map(FROM source);

}

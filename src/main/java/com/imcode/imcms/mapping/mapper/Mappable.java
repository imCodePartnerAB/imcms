package com.imcode.imcms.mapping.mapper;

@FunctionalInterface
public interface Mappable<FROM, TO> {

    TO map(FROM source);

}

package com.imcode.imcms.servlet.admin;

import java.io.Serializable;

public interface Handler<E> extends Serializable {
    void handle(E e) ;
}

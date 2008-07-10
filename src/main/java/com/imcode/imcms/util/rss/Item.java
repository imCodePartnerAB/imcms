package com.imcode.imcms.util.rss;

import java.util.Map;

import org.apache.commons.beanutils.DynaBean;

public interface Item {

    String getLink();

    String getTitle();

    String getDescription();

    Map<NameSpace,Map<String,String>> getNameSpaceStrings();

    Map<NameSpace, DynaBean> getNameSpaceBeans();
}
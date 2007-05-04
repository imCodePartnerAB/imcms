package com.imcode.imcms.util.rss;

import org.apache.commons.beanutils.DynaBean;

import java.util.Map;

public interface Item {

    String getLink();

    String getTitle();

    String getDescription();

    Map<NameSpace,Map<String,String>> getNameSpaceStrings();

    Map<NameSpace, DynaBean> getNameSpaceBeans();
}
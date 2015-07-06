package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 23.04.2015.
 */
public class CategoryApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
        JSONUtils.defaultJSONAnswer(resp,
                new HashSet<>(Arrays.asList(categoryMapper.getAllCategoryTypes())).stream()
                        .collect(
                                Collectors.toMap(
                                        CategoryTypeDomainObject::getName,
                                        val -> Stream.of(categoryMapper.getAllCategoriesOfType(val))
                                                .map(CategoryDomainObject::getName)
                                                .collect(Collectors.toList())
                                )
                        )
        );

    }
}

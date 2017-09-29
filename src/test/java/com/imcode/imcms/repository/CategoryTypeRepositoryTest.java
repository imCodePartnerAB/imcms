package com.imcode.imcms.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.util.datainitializer.CategoryDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryTypeRepositoryTest {

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Before
    public void initData() {
        categoryDataInitializer.init(4);
    }

    @Test
    public void invertCaseTest() {
        Assert.assertEquals("aBaBaB000", invertCase("AbAbAb000"));
    }

    @Test
    public void findByNameIgnoreCaseExpectedNotNullTest() {
        final List<CategoryType> types = categoryDataInitializer.getTypes();

        types.stream()
                .map(CategoryType::getName)
                .map(this::invertCase)
                .map(categoryTypeRepository::findByNameIgnoreCase)
                .forEach(Assert::assertNotNull);
    }


    private String invertCase(String str) {
        return str
                .chars()
                .map(charCode -> Character.isLowerCase(charCode)
                        ? Character.toUpperCase(charCode) : Character.toLowerCase(charCode))
                .mapToObj(charCode -> String.valueOf((char) charCode))
                .collect(Collectors.joining());
    }

}

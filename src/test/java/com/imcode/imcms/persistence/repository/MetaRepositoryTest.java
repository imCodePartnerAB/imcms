package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class MetaRepositoryTest {

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @After
    public void tearDown() {
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void findAllIds() {

        final List<Integer> createdIds = IntStream.range(0, 10)
                .mapToObj(i -> documentDataInitializer.createData().getId())
                .collect(Collectors.toList());

        assertTrue(metaRepository.findAllIds().containsAll(createdIds));

    }

    @Test
    public void findIdsBetween() {

        final Integer firstId = documentDataInitializer.createData().getId();

        final List<Integer> middleIds = IntStream.range(0, 10)
                .mapToObj(i -> documentDataInitializer.createData().getId())
                .collect(Collectors.toList());

        final Integer lastId = documentDataInitializer.createData().getId();

        final List<Integer> expectedIds = new ArrayList<>();
        expectedIds.add(firstId);
        expectedIds.addAll(middleIds);
        expectedIds.add(lastId);

        final List<Integer> idsBetween = metaRepository.findIdsBetween(firstId, lastId);

        assertEquals(idsBetween, expectedIds);

    }

    @Test
    public void findMinId() {

        //noinspection ResultOfMethodCallIgnored
        IntStream.range(0, 10)
                .mapToObj(i -> documentDataInitializer.createData())
                .collect(Collectors.toList());

        @SuppressWarnings("ConstantConditions") final int minId = metaRepository.findAll().stream().mapToInt(Meta::getId).min().getAsInt();

        assertEquals(Integer.valueOf(minId), metaRepository.findMinId());

    }

    @Test
    public void findMaxId() {

        //noinspection ResultOfMethodCallIgnored
        IntStream.range(0, 10)
                .mapToObj(i -> documentDataInitializer.createData())
                .collect(Collectors.toList());

        @SuppressWarnings("ConstantConditions") final int maxId = metaRepository.findAll().stream().mapToInt(Meta::getId).max().getAsInt();

        assertEquals(Integer.valueOf(maxId), metaRepository.findMaxId());

    }

    @Test
    public void findMinAndMaxId() {

        //noinspection ResultOfMethodCallIgnored
        IntStream.range(0, 10)
                .mapToObj(i -> documentDataInitializer.createData())
                .collect(Collectors.toList());

        final List<Meta> allDocs = metaRepository.findAll();

        @SuppressWarnings("ConstantConditions") final int minId = allDocs.stream().mapToInt(Meta::getId).min().getAsInt();

        @SuppressWarnings("ConstantConditions") final int maxId = allDocs.stream().mapToInt(Meta::getId).max().getAsInt();

        assertTrue(Arrays.asList(minId, maxId).containsAll(Arrays.asList(metaRepository.findMinAndMaxId())));

    }

    @Test
    public void findTarget() {

        final DocumentDTO createdDoc = documentDataInitializer.createData();

        assertEquals(createdDoc.getTarget(), metaRepository.findTarget(createdDoc.getId()));

    }
}
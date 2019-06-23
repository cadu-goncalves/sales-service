package com.viniland.sales.service;

import com.viniland.sales.domain.exception.AlbumException;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.persistence.AlbumRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link AlbumService}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AlbumServiceTest {

    @MockBean
    private AlbumRepository mockRepo;

    @Autowired
    private AlbumService service;

    private Album album;

    @Before
    public void beforeEach() {
        // Album fixture
        album = Album.builder()
                .id("1111")
                .name("Nice album")
                .genre("rock")
                .price(15.0)
                .build();

        // Reset mocks
        reset(mockRepo);
    }

    /**
     * Test scenario for album retrieve
     *
     * @throws Exception
     */
    @Test
    public void itRetrieveAlbum() throws Exception {
        //  Mock behaviours
        when(mockRepo.findById(eq("aaa"))).thenReturn(Optional.of(album));

        // Test
        Album resultA = service.retrieve("aaa").get();
        assertThat(resultA, equalTo(album));

        // Check mock iteration
        verify(mockRepo).findById(eq("aaa"));
    }

    /**
     * Test scenario for album retrieve not found
     *
     * @throws Exception
     */
    @Test
    public void itThrowsAlbumNotFound() throws Exception {
        //  Mock behaviours
        when(mockRepo.findById(eq("bbb"))).thenReturn(Optional.empty());

        // Test (must throw exception)
        try {
            service.retrieve("bbb").get();
            Assert.fail();
        } catch (Exception e) {
            if (!(e.getCause() instanceof AlbumException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario for album search
     *
     * @throws Exception
     */
    @Test
    public void itSearchesAlbums() throws Exception {
        // Input fixtures
        AlbumFilter filter = new AlbumFilter();
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest reqPage = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Output fixtures
        List<Album> content = new ArrayList<>();
        content.add(album);
        Page page = new PageImpl(content, reqPage, 1);

        //  Mock behaviours
        when(mockRepo.findAll(eq(reqPage))).thenReturn(page);

        // Test
        Page<Album> result = service.search(filter).get();
        assertThat(result.getContent(), hasItem(album));
        assertThat(result.getNumber(), equalTo(filter.getPage()));
        assertThat(result.getTotalPages(), equalTo(page.getTotalPages()));

        // Check mock iteration
        verify(mockRepo).findAll(eq(reqPage));
    }

    /**
     * Test scenario for album search
     *
     * @throws Exception
     */
    @Test
    public void itSearchesAlbumsFiltering() throws Exception {
        // Input fixtures
        AlbumFilter filter = new AlbumFilter();
        filter.setGenre("rock");
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest reqPage = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Output fixtures
        List<Album> content = new ArrayList<>();
        content.add(album);
        Page page = new PageImpl(content, reqPage, 1);

        //  Mock behaviours
        when(mockRepo.findAll(any(Example.class), eq(reqPage))).thenReturn(page);

        // Test
        Page<Album> result = service.search(filter).get();
        assertThat(result.getContent(), hasItem(album));
        assertThat(result.getNumber(), equalTo(filter.getPage()));
        assertThat(result.getTotalPages(), equalTo(page.getTotalPages()));

        // Check mock iteration
        verify(mockRepo).findAll(any(Example.class), eq(reqPage));
    }
}
package com.viniland.sales.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viniland.sales.domain.exception.AlbumException;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.service.AlbumService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link AlbumController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AlbumControllerTest {

    @MockBean
    private AlbumService mockService;

    @Autowired
    private AlbumController controller;

    @Autowired
    private MockMvc mockMvc;

    private Album album;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        album = new Album();
        album.setId("1111");
        album.setName("Nice album");
        album.setGenre("Rock");
        album.setPrice(15.0);

        // Reset mocks
        reset(mockService);
    }

    /**
     * Test scenario for GET album with result
     *
     * @throws Exception
     */
    @Test
    public void itHandlesGetAlbum() throws Exception {
        // Mock behaviours
        when(mockService.retrieve(album.getId())).thenReturn(CompletableFuture.completedFuture(album));

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/albums/{id}", album.getId());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(album.getId()))
                .andExpect(jsonPath("$.name").value(album.getName()))
                .andExpect(jsonPath("$.price").value(album.getPrice()));

        // Check mock iteration
        verify(mockService).retrieve(album.getId());
    }

    /**
     * Test scenario for GET album not found
     *
     * @throws Exception
     */
    @Test
    public void itHandlesGetAlbumNotFound() throws Exception {
        // Mock behaviours
        final CompletableFuture<Album> future = CompletableFuture.supplyAsync(() -> {
            throw new AlbumException("Error message 123", DomainError.RETRIEVE_ERROR);
        });
        when(mockService.retrieve(album.getId())).thenReturn(future);

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/albums/{id}", album.getId());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Error message 123")));

        // Check mock iteration
        verify(mockService).retrieve(album.getId());
    }

}
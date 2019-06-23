package com.viniland.sales.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.viniland.sales.domain.exception.AlbumException;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.service.AlbumService;
import com.viniland.sales.util.MessageUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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

    private AlbumFilter filter;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        album = Album.builder()
                .id("1111")
                .name("Nice album")
                .genre("Rock")
                .price(15.0)
                .build();

        filter = new AlbumFilter();

        // Reset mocks
        reset(mockService);
    }

    /**
     * Test scenario for GET album with result
     *
     * @throws Exception
     */
    @Test
    public void itHandlesRetrieve() throws Exception {
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
    public void itHandlesRetrieveNotFound() throws Exception {
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

    /**
     * Test scenario for POST search with invalid filter
     *
     * @throws Exception
     */
    @Test
    public void itHandlesInvalidSearch() throws Exception {
        // Invalid payload
        filter.setPage(-1);
        filter.setSize(1000);
        String payload = mapper.writeValueAsString(filter);

        // Expected messages (attached by CustomHandler)
        String msgPassword = MessageUtils.getMessage("messages", "filter.page.invalid");
        String msgProfile = MessageUtils.getMessage("messages", "filter.size.invalid");

        // Request
        MockHttpServletRequestBuilder reqBuilder = post("/api/albums/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call & Check
        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(msgPassword)))
                .andExpect(content().string(containsString(msgProfile)));
    }

    /**
     *  Test scenario for POST search with valid filter
     *
     * @throws Exception
     */
    @Test
    public void itHandlesSearch() throws Exception {
        // Mock behaviours
        PageImpl<Album> page = new PageImpl(Lists.newArrayList(album), PageRequest.of(0, 10), 0);
        when(mockService.search(any(AlbumFilter.class))).thenReturn(CompletableFuture.completedFuture(page));

        // Request
        String payload = mapper.writeValueAsString(filter);
        MockHttpServletRequestBuilder reqBuilder = post("/api/albums/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page.getNumber()))
                .andExpect(jsonPath("$.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.matches").value(page.getNumberOfElements()))
                .andExpect(jsonPath("$.content[0].id").value(page.getContent().get(0).getId()))
                .andExpect(jsonPath("$.content[0].name").value(page.getContent().get(0).getName()))
                .andExpect(jsonPath("$.content[0].price").value(page.getContent().get(0).getPrice()));

        // Check mock iteration
        verify(mockService).search(any(AlbumFilter.class));
    }
}
package com.viniland.sales.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.exception.SaleException;
import com.viniland.sales.domain.model.Sale;
import com.viniland.sales.domain.rest.filter.SaleFilter;
import com.viniland.sales.service.SaleService;
import com.viniland.sales.util.MessageUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link SaleController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SaleControllerTest {

    @MockBean
    private SaleService mockService;

    @Autowired
    private SaleController controller;

    @Autowired
    private MockMvc mockMvc;

    private Sale sale;

    private SaleFilter filter;

    private ObjectMapper mapper;

    @Before
    public void beforeEach() {
        // Fixtures
        mapper = new ObjectMapper();

        // Calendar instance from UTC fails, set timezone first
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date nowUtc = Calendar.getInstance().getTime();

        sale = Sale.builder()
                .id("1")
                .customerId(30L)
                .register(nowUtc)
                .item(new Sale.SaleItem("xxx", 12.1D, 3.1D, 1))
                .item(new Sale.SaleItem("bbb", 22.12D, 4.2D, 3))
                .build();

        filter = new SaleFilter();

        // Reset mocks
        reset(mockService);
    }
    /**
     * Test scenario for GET sale with result
     *
     * @throws Exception
     */
    @Test
    public void itHandlesRetrieve() throws Exception {
        // Mock behaviours
        when(mockService.retrieve(sale.getId())).thenReturn(CompletableFuture.completedFuture(sale));

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/sales/{id}", sale.getId());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$.items[0].id").value("xxx"))
                .andExpect(jsonPath("$.items[0].quantity").value(1))
                .andExpect(jsonPath("$.items[1].id").value("bbb"))
                .andExpect(jsonPath("$.items[1].quantity").value(3));

        // Check mock iteration
        verify(mockService).retrieve(sale.getId());
    }

    /**
     * Test scenario for GET sale not found
     *
     * @throws Exception
     */
    @Test
    public void itHandlesRetrieveNotFound() throws Exception {
        // Mock behaviours
        final CompletableFuture<Sale> future = CompletableFuture.supplyAsync(() -> {
            throw new SaleException("Error message here", DomainError.RETRIEVE_ERROR);
        });
        when(mockService.retrieve(sale.getId())).thenReturn(future);

        // Request
        MockHttpServletRequestBuilder reqBuilder = get("/api/sales/{id}", sale.getId());

        // Call
        MvcResult result = mockMvc.perform(reqBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();

        // Check
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Error message here")));

        // Check mock iteration
        verify(mockService).retrieve(sale.getId());
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

        // Expected messages (attached by ErrorController)
        String msgPage = MessageUtils.getMessage("messages", "filter.page.invalid");
        String msgSize = MessageUtils.getMessage("messages", "filter.size.invalid");

        // Request
        MockHttpServletRequestBuilder reqBuilder = post("/api/sales/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Call & Check
        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(msgPage)))
                .andExpect(content().string(containsString(msgSize)));
    }

    /**
     * Test scenario for GET sale search
     *
     * @throws Exception
     */
    @Test
    public void itHandlesSearch() throws Exception {
        // Mock behaviours
        List<Sale> content = new ArrayList<>();
        content.add(sale);
        Page page = new PageImpl(content, Pageable.unpaged(), 1);
        when(mockService.search(filter)).thenReturn(CompletableFuture.completedFuture(page));

        // Request
        String payload = mapper.writeValueAsString(filter);
        MockHttpServletRequestBuilder reqBuilder = post("/api/sales/search")
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
                .andExpect(jsonPath("$.page").value(filter.getPage()))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.matches").value(1))
                .andExpect(jsonPath("$.content[0].id").value(sale.getId()))
                .andExpect(jsonPath("$.content[0].items[0].id").value("xxx"))
                .andExpect(jsonPath("$.content[0].items[1].id").value("bbb"));

        // Check mock iteration
        verify(mockService).search(filter);
    }
}

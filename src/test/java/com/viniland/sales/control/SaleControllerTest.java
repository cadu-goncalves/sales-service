package com.viniland.sales.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.exception.SaleException;
import com.viniland.sales.domain.model.Sale;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.service.SaleService;
import org.junit.Before;
import org.junit.Ignore;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private AlbumFilter filter;

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

        filter = new AlbumFilter();

        // Reset mocks
        reset(mockService);
    }
    /**
     * Test scenario for GET sale with result
     *
     * @throws Exception
     */
    @Test
    @Ignore
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
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$.register").value(df.format(sale.getRegister())))
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
}

package com.viniland.sales.control;

import com.viniland.sales.domain.model.Sale;
import com.viniland.sales.domain.rest.ResourceError;
import com.viniland.sales.domain.rest.SaleResource;
import com.viniland.sales.domain.rest.filter.SaleFilter;
import com.viniland.sales.domain.rest.page.ResourcePage;
import com.viniland.sales.service.SaleService;
import com.viniland.sales.util.rest.ResourceBuilder;
import com.viniland.sales.util.rest.ResourceErrorBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

/**
 * Sale API controller
 */
@Api
@RestController
@RequestMapping(
        value = "api/sales/",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE
        })
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @ApiOperation(value = "Recover Sale", response = SaleResource.class)
    @GetMapping(value = "{id}")
    public @ResponseBody
    DeferredResult<ResponseEntity> recoverSale(@PathVariable String id) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Sale> future = service.retrieve(id);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ResourceError error = ResourceErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        SaleResource resouce = ResourceBuilder.asResource(result, SaleResource.class);
                        response.setResult(new ResponseEntity<>(resouce, HttpStatus.OK));
                    }
                }
        );
        return response;
    }

    @ApiOperation(value = "Search Sales", response = ResourcePage.class)
    @PostMapping(value = "search")
    public @ResponseBody
    DeferredResult<ResponseEntity> searchSales(@RequestBody @Validated SaleFilter filter) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Page<Sale>> future = service.search(filter);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ResourceError error = ResourceErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        ResourcePage<SaleResource> resource = ResourceBuilder.asPage(result, SaleResource.class);
                        response.setResult(new ResponseEntity<>(resource, HttpStatus.OK));
                    }
                }
        );

        return response;
    }

    @ApiOperation(value = "Register Sale", response = SaleResource.class)
    @PostMapping
    public @ResponseBody
    DeferredResult<ResponseEntity> registerSale(@RequestBody @Validated SaleResource body) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Sale> future = service.register(body);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ResourceError error = ResourceErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        SaleResource resource = ResourceBuilder.asResource(result, SaleResource.class);
                        response.setResult(new ResponseEntity<>(resource, HttpStatus.OK));
                    }
                }
        );

        return response;
    }
}

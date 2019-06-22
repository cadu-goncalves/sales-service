package com.viniland.sales.control;

import com.viniland.sales.domain.rest.SaleResource;
import com.viniland.sales.domain.rest.filter.SaleFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

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

    @ApiOperation(value = "Recover Sale")
    @GetMapping(value = "{id}")
    public @ResponseBody
    DeferredResult<ResponseEntity> recoverSale(@RequestParam Long id) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        return response;
    }

    @ApiOperation(value = "Search Sales")
    @PostMapping(value = "search")
    public @ResponseBody
    DeferredResult<ResponseEntity> searchSales(@RequestBody @Validated SaleFilter filter) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        return response;
    }

    @ApiOperation(value = "Register Sale")
    @PostMapping
    public @ResponseBody
    DeferredResult<ResponseEntity> registerSale(@RequestBody @Validated SaleResource body) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        return response;
    }
}

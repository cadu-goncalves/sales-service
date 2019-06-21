package com.viniland.sales.control;

import com.viniland.sales.domain.rest.filter.AlbumResouceFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Album API controller
 */
@Api
@RestController
@RequestMapping(
        value = "api/albums/",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE
        })
public class AlbumController {

    @ApiOperation(value = "Recover Album")
    @GetMapping(value = "{id}")
    public @ResponseBody
    DeferredResult<ResponseEntity> recoverAlbum(@RequestParam String id) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        return response;
    }

    @ApiOperation(value = "Search Albums")
    @PostMapping(value = "search")
    public @ResponseBody
    DeferredResult<ResponseEntity> searchAlbums(@RequestBody @Validated AlbumResouceFilter filter) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        return response;
    }
}

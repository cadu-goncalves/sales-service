package com.viniland.sales.control;

import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.rest.AlbumResource;
import com.viniland.sales.domain.rest.ResourceError;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.domain.rest.page.ResourcePage;
import com.viniland.sales.service.AlbumService;
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

    private final AlbumService service;

    public AlbumController(AlbumService service) {
        this.service = service;
    }

    @ApiOperation(value = "Recover Album", response = AlbumResource.class)
    @GetMapping(value = "{id}")
    public @ResponseBody
    DeferredResult<ResponseEntity> recoverAlbum(@PathVariable String id) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Album> future = service.retrieve(id);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ResourceError error = ResourceErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        AlbumResource resouce = ResourceBuilder.asResource(result, AlbumResource.class);
                        response.setResult(new ResponseEntity<>(resouce, HttpStatus.OK));
                    }
                }
        );
        return response;
    }

    @ApiOperation(value = "Search Albums", response = ResourcePage.class)
    @PostMapping(value = "search")
    public @ResponseBody
    DeferredResult<ResponseEntity> searchAlbums(@RequestBody @Validated AlbumFilter filter) {
        DeferredResult<ResponseEntity> response = new DeferredResult<>();

        CompletableFuture<Page<Album>> future = service.search(filter);
        future.whenCompleteAsync(
                (result, throwable) -> {
                    if (throwable != null) {
                        ResourceError error = ResourceErrorBuilder.build(throwable);
                        response.setErrorResult(new ResponseEntity<>(error, error.getStatus()));
                    } else {
                        ResourcePage<AlbumResource> resource = ResourceBuilder.asPage(result, AlbumResource.class);
                        response.setResult(new ResponseEntity<>(resource, HttpStatus.OK));
                    }
                }
        );

        return response;
    }

}

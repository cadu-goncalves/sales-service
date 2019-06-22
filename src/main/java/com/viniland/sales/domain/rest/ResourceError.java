package com.viniland.sales.domain.rest;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Resource error
 */
@ApiModel
@Data
@NoArgsConstructor
public final class ResourceError {

    @JsonIgnore
    private HttpStatus status;

    private Date timestamp = new Date();

    private Set<String> messages = new HashSet<>();

    @JsonGetter("status")
    public Integer getStatusCode() {
        if(status == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        return status.value();
    }

    public void addMessages(Collection messages) {
        this.messages.addAll(messages);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}

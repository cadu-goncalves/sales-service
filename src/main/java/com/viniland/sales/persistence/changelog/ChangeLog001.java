package com.viniland.sales.persistence.changelog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.viniland.sales.domain.model.AlbumCashBack;
import lombok.extern.slf4j.Slf4j;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

/**
 * Database change logs 001
 */
@ChangeLog(order = "001")
@Slf4j
public class ChangeLog001 {

    /**
     * Insert album cashbacks
     *
     * @param jongo {@link Jongo}
     */
    @ChangeSet(order = "001", id = "Albuns cashback", author = "cadu.goncalves")
    public void insertCashbacks(Jongo jongo) {
        String name = AlbumCashBack.class.getAnnotation(Document.class).collection();
        MongoCollection collection = jongo.getCollection(name);

        try {
            File file = ResourceUtils.getFile("classpath:changelog/001/data.json");
            ArrayNode data = (ArrayNode) new ObjectMapper().readTree(file);
            data.elements().forEachRemaining(e -> {
                collection.insert(e);
            });

        } catch (IOException e) {
            log.error("Unable to perform migration", e);
        }
    }

}

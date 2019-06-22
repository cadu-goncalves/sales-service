package com.viniland.sales.persistence;

import com.viniland.sales.domain.model.Album;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Album repository
 *
 * <p>Spring Data builds the implementation dynamically</p>
 */
@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album, String>, QueryByExampleExecutor<Album> {
}

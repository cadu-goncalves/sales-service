package com.viniland.sales.persistence;

import com.viniland.sales.domain.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SaleRepository extends PagingAndSortingRepository<Sale, String>, QueryByExampleExecutor<Sale> {

    @Query("{ 'register': { $lt : ?0 } }")
    Page<Sale> findByRegisterTo(Date to, Pageable page);

    @Query("{ 'register': { $gte : ?0 } }")
    Page<Sale> findByRegisterFrom(Date from,  Pageable page);

    Page<Sale> findByRegisterBetween(Date from, Date to, Pageable page);

}

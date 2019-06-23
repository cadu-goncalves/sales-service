package com.viniland.sales.persistence;

import com.viniland.sales.domain.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SaleRepository extends PagingAndSortingRepository<Sale, String>, QueryByExampleExecutor<Sale> {

    Page<Sale> findByRegisterBetween(Date from, Date to, PageRequest page);
}

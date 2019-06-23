package com.viniland.sales.persistence;

import com.viniland.sales.domain.model.CashbackOffer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackOfferRepository extends PagingAndSortingRepository<CashbackOffer, CashbackOffer.WeekDay> {
}

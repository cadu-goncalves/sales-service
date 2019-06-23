package com.viniland.sales.util;

import com.viniland.sales.domain.model.CashbackOffer;
import lombok.experimental.UtilityClass;

import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class WeekDayUtils {

    public static CashbackOffer.WeekDay fromDate(Date date) {
        String weekDayName = date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .getDayOfWeek().name();
       return CashbackOffer.WeekDay.valueOf(weekDayName);
    }
}

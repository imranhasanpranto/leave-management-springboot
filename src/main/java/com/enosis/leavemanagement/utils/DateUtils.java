package com.enosis.leavemanagement.utils;

import com.enosis.leavemanagement.dto.LeaveDaysDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DateUtils {
    public LeaveDaysDTO getAllLeaveDates(LocalDate fromDate, LocalDate toDate, Set<Long> blockedDateSet){
        List<LocalDate> localDateList = new ArrayList<>();
        LocalDate weekday = fromDate;
        int leaveDays = 0;
        while (weekday.isBefore(toDate)) {
            if(!blockedDateSet.contains(weekday.toEpochDay())){
                leaveDays++;
                localDateList.add(weekday);
            }
            if (weekday.getDayOfWeek() == DayOfWeek.FRIDAY)
                weekday = weekday.plusDays(3);
            else
                weekday = weekday.plusDays(1);
        }
        if(!blockedDateSet.contains(toDate.toEpochDay())){
            leaveDays++;
            localDateList.add(toDate);
        }

        return LeaveDaysDTO.builder()
                .leaveDays(localDateList)
                .leaveCount(leaveDays)
                .build();
    }
}

package com.enosis.leavemanagement.interfaces;

import java.time.LocalDateTime;

public interface ProjectDateRange {
    LocalDateTime getFromDate();
    LocalDateTime getToDate();

    void setFromDate(LocalDateTime fromDate);
    void setToDate(LocalDateTime toDate);
}

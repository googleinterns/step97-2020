package com.google.sps.data;
import java.time.Period;
import java.time.LocalDate;

public boolean isExpiredAnalysis(LocalDate then){
    LocalDate now = LocalDate.now();
    Period diff = Period.between(then, now);
    if(diff.days() >= 7){
        return true;
    }else {
        return false;
    }
}
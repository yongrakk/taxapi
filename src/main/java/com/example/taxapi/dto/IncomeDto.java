package com.example.taxapi.dto;

import com.example.taxapi.domain.Income;
import com.example.taxapi.domain.pk.IncomePk;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class IncomeDto {
    private String userId;
    private String year;
    private Double incomeAmount;

    @Builder
    public Income toEntity(){
        IncomePk incomePk = IncomePk.builder()
                .incomeYear(year)
                .userId(userId)
                .build();

        Income income = Income.builder()
                .incomePk(incomePk)
                .incomeAmount(incomeAmount).build();
        return income;
    }
}

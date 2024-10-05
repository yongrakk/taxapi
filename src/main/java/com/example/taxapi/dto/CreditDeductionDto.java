package com.example.taxapi.dto;

import com.example.taxapi.domain.CreditDeduction;
import com.example.taxapi.domain.pk.CreditDeductionPk;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CreditDeductionDto {
    private String year;
    private String month;
    private String userId;
    private Double creditDeductionAmount;

    @Builder
    public CreditDeduction toEntity(){
        CreditDeductionPk creditDeductionPk = CreditDeductionPk.builder()
                .creditDeductionYear(year)
                .creditDeductionMonth(month)
                .userId(userId)
                .build();
        CreditDeduction creditDeduction = CreditDeduction.builder()
                .creditDeductionPk(creditDeductionPk)
                .creditDeductionAmount(creditDeductionAmount)
                .build();

        return creditDeduction;
    }
}

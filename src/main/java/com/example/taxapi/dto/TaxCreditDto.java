package com.example.taxapi.dto;

import com.example.taxapi.domain.TaxCredit;
import com.example.taxapi.domain.pk.TaxCreditPk;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TaxCreditDto {

    private String year;
    private String userId;
    private Double taxCreditAmount;

    @Builder
    public TaxCredit toEntity(){
        TaxCreditPk taxCreditPk = TaxCreditPk.builder()
                .taxCreditYear(year)
                .userId(userId)
                .build();
        TaxCredit taxCredit = TaxCredit.builder()
                .taxCreditPk(taxCreditPk)
                .taxCreditAmount(taxCreditAmount)
                .build();

        return taxCredit;
    }

}

package com.example.taxapi.dto;

import com.example.taxapi.domain.Pension;
import com.example.taxapi.domain.pk.PensionPk;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class PensionDto {

    private String year;
    private String month;
    private String userId;
    private Double pensionAmount;

    @Builder
    public Pension toEntity(){
        PensionPk pensionPk = new PensionPk();
        pensionPk.setPensionYear(year);
        pensionPk.setPensionMonth(month);
        pensionPk.setUserId(userId);

        Pension pension = Pension.builder()
                .pensionPk(pensionPk)
                .pensionAmount(pensionAmount)
                .build();

        return pension;
    }
}

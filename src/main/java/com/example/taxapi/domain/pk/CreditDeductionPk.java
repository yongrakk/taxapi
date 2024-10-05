package com.example.taxapi.domain.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class CreditDeductionPk implements Serializable {

    @Column(name = "creditDeductionYear")
    private String creditDeductionYear;

    @Column(name = "creditDeductionMonth")
    private String creditDeductionMonth;

    @Column(name = "userId")
    private String userId;
}

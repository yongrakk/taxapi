package com.example.taxapi.domain;

import com.example.taxapi.domain.pk.CreditDeductionPk;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table
public class CreditDeduction {

    @EmbeddedId
    private CreditDeductionPk creditDeductionPk;

    @Column(nullable = false)
    private Double creditDeductionAmount;

}

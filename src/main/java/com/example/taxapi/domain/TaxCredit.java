package com.example.taxapi.domain;

import com.example.taxapi.domain.pk.TaxCreditPk;
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
public class TaxCredit {

    @EmbeddedId
    private TaxCreditPk taxCreditPk;

    @Column(nullable = false)
    private Double taxCreditAmount;

}

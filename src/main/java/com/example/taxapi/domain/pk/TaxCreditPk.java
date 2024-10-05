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
public class TaxCreditPk implements Serializable {

    @Column(name = "taxCreditYear")
    private String taxCreditYear;

    @Column(name = "userId")
    private String userId;

}

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
public class IncomePk implements Serializable {

    @Column(name="incomeYear")
    private String incomeYear;

    @Column(name="userId")
    private String userId;

}

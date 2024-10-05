package com.example.taxapi.domain;

import com.example.taxapi.domain.pk.IncomePk;
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
public class Income {

    @EmbeddedId
    private IncomePk incomePk;

    @Column(nullable = false)
    private Double incomeAmount;

}

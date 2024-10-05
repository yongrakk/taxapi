package com.example.taxapi.domain;


import com.example.taxapi.domain.pk.PensionPk;
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
public class Pension {

    @EmbeddedId
    private PensionPk pensionPk;

    @Column(nullable = false)
    private Double pensionAmount;

}

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
public class PensionPk implements Serializable {

    @Column(name = "pensionYear")
    private String pensionYear;

    @Column(name="pensionMonth")
    private String pensionMonth;

    @Column(name = "userId")
    private String userId;

}

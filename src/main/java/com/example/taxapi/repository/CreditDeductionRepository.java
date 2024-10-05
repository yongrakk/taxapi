package com.example.taxapi.repository;

import com.example.taxapi.domain.CreditDeduction;
import com.example.taxapi.domain.pk.CreditDeductionPk;
import com.example.taxapi.dto.CreditDeductionSumDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditDeductionRepository extends JpaRepository<CreditDeduction, CreditDeductionPk> {

    @Query(value =
            "SELECT new com.example.taxapi.dto.CreditDeductionSumDto(SUM(cd.creditDeductionAmount))"
                    + "FROM CreditDeduction cd "
                    + "WHERE cd.creditDeductionPk.userId = :userId AND cd.creditDeductionPk.creditDeductionYear = :creditDeductionYear "
                    + "GROUP BY cd.creditDeductionPk.userId, cd.creditDeductionPk.creditDeductionYear"
    )
    CreditDeductionSumDto findGroupByUserIdAndYear(@Param("userId") String userId, @Param("creditDeductionYear") String year);
}

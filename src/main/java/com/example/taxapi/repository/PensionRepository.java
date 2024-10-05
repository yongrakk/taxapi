package com.example.taxapi.repository;

import com.example.taxapi.domain.Pension;
import com.example.taxapi.domain.pk.PensionPk;
import com.example.taxapi.dto.PensionSumDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PensionRepository extends JpaRepository<Pension, PensionPk> {

    @Query(value =
            "SELECT new com.example.taxapi.dto.PensionSumDto(SUM(p.pensionAmount))"
                    + "FROM Pension p "
                    + "WHERE p.pensionPk.userId = :userId AND p.pensionPk.pensionYear = :pensionYear "
                    + "GROUP BY p.pensionPk.userId , p.pensionPk.pensionYear"
    )
    PensionSumDto findGroupByUserIdAndYear(@Param("userId") String userId , @Param("pensionYear") String pensionYear);

}

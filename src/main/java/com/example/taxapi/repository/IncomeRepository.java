package com.example.taxapi.repository;

import com.example.taxapi.domain.Income;
import com.example.taxapi.domain.pk.IncomePk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, IncomePk> {

}

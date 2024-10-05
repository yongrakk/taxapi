package com.example.taxapi.repository;

import com.example.taxapi.domain.TaxCredit;
import com.example.taxapi.domain.pk.TaxCreditPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxCreditRepository extends JpaRepository<TaxCredit, TaxCreditPk> {
}

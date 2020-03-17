package ru.alibaev.quotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alibaev.quotation.entity.Quote;

@Repository
public interface QuotationRepository extends JpaRepository<Quote, Long> {
}

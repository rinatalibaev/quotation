package ru.alibaev.quotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alibaev.quotation.entity.EnergyLevel;

@Repository
public interface EnergyLevelRepository extends JpaRepository<EnergyLevel, String> {
    EnergyLevel findByIsin(String isin);
}

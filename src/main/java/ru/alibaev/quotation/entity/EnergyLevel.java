package ru.alibaev.quotation.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alibaev.quotation.dto.EnergyLevelDto;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Сущность лучшей цены по ценной бумаге
 */
@Entity
@Data
@NoArgsConstructor
public class EnergyLevel {

    /**
     * международный идентификационный код ценной бумаги
     */
    @Id
    @Size(min = 12, max = 12)
    private String isin;

    /**
     * лучшая цена по данной ценной бумаге
     */
    private BigDecimal elvl;

    @Transient
    @OneToOne
    private Quote quote;

    public EnergyLevelDto toDto() {
        EnergyLevelDto energyLevelDto = new EnergyLevelDto();
        energyLevelDto.setIsin(isin);
        energyLevelDto.setElvl(elvl);
        return energyLevelDto;
    }

}

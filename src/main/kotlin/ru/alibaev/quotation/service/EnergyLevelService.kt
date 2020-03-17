package ru.alibaev.quotation.service

import org.springframework.stereotype.Service
import ru.alibaev.quotation.dto.EnergyLevelDto
import ru.alibaev.quotation.entity.EnergyLevel
import ru.alibaev.quotation.repository.EnergyLevelRepository
import java.util.stream.Collectors

@Service
class EnergyLevelService(private val energyLevelRepository: EnergyLevelRepository?) {

    /**
     * Возвращает лучшую цену по идентификационному коду
     * @param isin международный идентификационный код ценной бумаги
     * @return данные о лучшей цене по идентификационному коду
     */
    fun getByIsin(isin: String?): EnergyLevelDto? {
        return energyLevelRepository!!.findByIsin(isin).toDto()
    }

    /**
     * Возвращает данные о лучших ценах по всем инструментам, по которым были получены котировки
     * @return elvl's по всем инструментам, по которым были получены котировки
     */
    fun getAll(): List<EnergyLevelDto?>? {
        return energyLevelRepository!!.findAll().stream()
            .map { obj: EnergyLevel -> obj.toDto() }.collect(
                Collectors.toList()
            )
    }
}
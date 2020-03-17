package ru.alibaev.quotation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.alibaev.quotation.dto.EnergyLevelDto
import ru.alibaev.quotation.service.EnergyLevelService

@RestController
@RequestMapping("/energy-level")
class EnergyLevelController(private val energyLevelService: EnergyLevelService?) {

    /**
     * Возвращает данные о лучшей цене по данному инструменту
     * @param isin международный идентификационный код ценной бумаги
     * @return объект дата-трансфер для уровня elvl по данному инструменту
     */
    @GetMapping
    fun energyLevel(@RequestParam isin: String?): EnergyLevelDto? {
        return energyLevelService!!.getByIsin(isin)
    }

    /**
     * Возвращает данные о лучших ценах по всем инструментам, по которым были получены котировки
     * @return elvl's по всем инструментам, по которым были получены котировки
     */
    @GetMapping("/all")
    fun energyLevels(): List<EnergyLevelDto?>? {
        return energyLevelService!!.getAll()
    }


}
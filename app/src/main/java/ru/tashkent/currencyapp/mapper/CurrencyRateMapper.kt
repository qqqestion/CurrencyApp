package ru.tashkent.currencyapp.mapper

import ru.tashkent.currencyapp.data.CurrencyRate
import ru.tashkent.currencyapp.data.db.CurrencyEntity
import ru.tashkent.currencyapp.data.remote.CurrencyRateRemote
import ru.tashkent.currencyapp.ui.common.CurrencyRateUi
import javax.inject.Inject

class CurrencyRateMapper @Inject constructor() {
    fun toUi(currencyRate: CurrencyRate) = CurrencyRateUi(
        currencyRate.symbol,
        "%.4f".format(currencyRate.rate),
        currencyRate.name,
        currencyRate.isFavourite
    )

    fun fromEntity(currencyRate: CurrencyEntity) = CurrencyRate(
        currencyRate.symbol,
        currencyRate.rate,
        currencyRate.name,
        currencyRate.isFavourite
    )
}
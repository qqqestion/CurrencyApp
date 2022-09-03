package ru.tashkent.currencyapp.ui.common

import androidx.recyclerview.widget.DiffUtil

class CurrencyRateDiffUtilCallback : DiffUtil.ItemCallback<CurrencyRateUi>() {
    override fun areItemsTheSame(oldItem: CurrencyRateUi, newItem: CurrencyRateUi) =
        oldItem.symbol == newItem.symbol

    override fun areContentsTheSame(oldItem: CurrencyRateUi, newItem: CurrencyRateUi) =
        oldItem == newItem
}
package ru.tashkent.currencyapp.ui.settings

import androidx.recyclerview.widget.DiffUtil

class CurrencyDiffUtilCallback : DiffUtil.ItemCallback<CurrencyNameUi>() {
    override fun areItemsTheSame(oldItem: CurrencyNameUi, newItem: CurrencyNameUi) =
        oldItem.symbol == newItem.symbol

    override fun areContentsTheSame(oldItem: CurrencyNameUi, newItem: CurrencyNameUi) =
        oldItem == newItem
}
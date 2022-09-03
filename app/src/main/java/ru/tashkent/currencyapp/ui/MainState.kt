package ru.tashkent.currencyapp.ui

import ru.tashkent.currencyapp.data.SortOption

data class MainState(
    val currencySymbol: String = "",
    val isSortVisible: Boolean = true,
    val sortOptions: List<SortOption> = emptyList(),
    val currentSort: SortOption? = null
)
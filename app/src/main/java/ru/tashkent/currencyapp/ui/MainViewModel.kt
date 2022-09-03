package ru.tashkent.currencyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.data.SortOption
import ru.tashkent.currencyapp.data.repository.CurrencyRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {
    private val currentDestination = MutableStateFlow(BottomNavigationDestination.Popular)
    private val sort = MutableStateFlow(SortOption.ALPHABETICAL_ASCENDING)
    private val sortOptions = listOf(
        SortOption.ALPHABETICAL_ASCENDING,
        SortOption.ALPHABETICAL_DESCENDING,
        SortOption.RATE_ASCENDING,
        SortOption.RATE_DESCENDING,
    )
    val state = combine(
        currencyRepository.observeBaseCurrency(),
        currentDestination,
        sort
    ) { baseCurrency, destination, sort ->
        MainState(
            currencySymbol = baseCurrency,
            isSortVisible = destination in setOf(
                BottomNavigationDestination.Popular, BottomNavigationDestination.Favourite
            ),
            sortOptions = sortOptions,
            currentSort = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(2000), MainState())

    fun setCurrentItem(item: BottomNavigationDestination) {
        currentDestination.value = item
    }

    fun setSort(sortOption: SortOption) {
        sort.value = sortOption
        viewModelScope.launch {
            currencyRepository.setSortOption(sortOption)
        }
    }
}
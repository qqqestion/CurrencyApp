package ru.tashkent.currencyapp.ui.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.R
import ru.tashkent.currencyapp.databinding.FragmentFavouriteBinding
import ru.tashkent.currencyapp.ui.common.CurrencyRateAdapter
import ru.tashkent.currencyapp.ui.common.OffsetItemDecoration
import ru.tashkent.currencyapp.ui.common.UiError
import ru.tashkent.currencyapp.ui.common.snackbar
import kotlin.math.roundToInt

@AndroidEntryPoint
class FavouriteFragment : Fragment(R.layout.fragment_favourite) {
    private val binding by viewBinding<FragmentFavouriteBinding>()

    private val viewModel by viewModels<FavouriteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currencyRateAdapter = CurrencyRateAdapter(viewModel::toggleFavourite)
        with(binding.rvCurrencyRates) {
            adapter = currencyRateAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            addItemDecoration(
                OffsetItemDecoration(
                    resources.getDimension(R.dimen.padding_s).roundToInt(),
                    resources.getDimension(R.dimen.padding_xxs).roundToInt(),
                    resources.getDimension(R.dimen.padding_xxs).roundToInt(),
                )
            )
        }
        binding.root.setOnRefreshListener {
            binding.root.isRefreshing = false
            viewModel.refresh()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    currencyRateAdapter.submitList(state.currencies)
                    binding.root.isRefreshing = state.isLoading
                    binding.tvEmptyLabel.isVisible = state.currencies.isEmpty()
                    state.error?.let { error ->
                        val message = when (error) {
                            is UiError.ResourceError -> getString(error.messageId)
                            is UiError.SimpleError -> error.message
                        }
                        snackbar(message) { viewModel.dismissError() }.show()
                    }
                }
            }
        }
    }
}
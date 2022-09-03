package ru.tashkent.currencyapp.ui.popular

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.R
import ru.tashkent.currencyapp.databinding.FragmentPopularBinding
import ru.tashkent.currencyapp.ui.common.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class PopularFragment : Fragment(R.layout.fragment_popular) {
    private val binding by viewBinding<FragmentPopularBinding>()

    private val viewModel by viewModels<PopularViewModel>()

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
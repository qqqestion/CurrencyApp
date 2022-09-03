package ru.tashkent.currencyapp.ui.settings

import android.os.Bundle
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
import ru.tashkent.currencyapp.databinding.FragmentSettingsBinding
import ru.tashkent.currencyapp.ui.common.OffsetItemDecoration
import ru.tashkent.currencyapp.ui.common.UiError
import ru.tashkent.currencyapp.ui.common.UiUtils.dpToPx
import ru.tashkent.currencyapp.ui.common.snackbar
import kotlin.math.roundToInt

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding by viewBinding<FragmentSettingsBinding>()

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currencyAdapter = CurrencyAdapter(viewModel::selectCurrency)
        with(binding.rvCurrencies) {
            adapter = currencyAdapter
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
        binding.root.isEnabled = false

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.swipeRefreshLayout.isRefreshing = state.isLoading
                    currencyAdapter.submitList(state.currencies) {
                        if (state.scrollToPosition != -1) {
                            binding.rvCurrencies.scrollToPosition(state.scrollToPosition)
                        }
                    }
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
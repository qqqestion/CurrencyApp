package ru.tashkent.currencyapp.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tashkent.currencyapp.databinding.ItemCurrencyBinding

class CurrencyAdapter(
    private val onCurrency: (CurrencyNameUi) -> Unit
) : ListAdapter<CurrencyNameUi, CurrencyAdapter.CurrencyViewHolder>(CurrencyDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CurrencyViewHolder(
            ItemCurrencyBinding.inflate(layoutInflater, parent, false)
        )

    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position), onCurrency)
    }

    class CurrencyViewHolder(
        private val binding: ItemCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyNameUi, onCurrency: (CurrencyNameUi) -> Unit) {
            binding.tvSymbol.text = item.symbol
            binding.tvDescription.text = item.name
            binding.root.setOnClickListener { onCurrency(item) }
            binding.ivCheck.isVisible = item.isSelected
        }
    }
}
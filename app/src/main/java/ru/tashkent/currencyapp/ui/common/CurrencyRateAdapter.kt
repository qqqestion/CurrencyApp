package ru.tashkent.currencyapp.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tashkent.currencyapp.R
import ru.tashkent.currencyapp.databinding.ItemCurrencyRateBinding

class CurrencyRateAdapter(
    private val onFavourite: (CurrencyRateUi) -> Unit
) : ListAdapter<CurrencyRateUi, CurrencyRateAdapter.CurrencyRateViewHolder>(
    CurrencyRateDiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CurrencyRateViewHolder(
            ItemCurrencyRateBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyRateViewHolder, position: Int) {
        holder.bind(getItem(position), onFavourite)
    }

    class CurrencyRateViewHolder(
        private val binding: ItemCurrencyRateBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyRateUi, onFavourite: (CurrencyRateUi) -> Unit) {
            binding.ivFavourite.setOnClickListener { onFavourite(item) }
            binding.tvSymbol.text = item.symbol
            binding.tvRate.text = item.rate
            binding.tvName.text = item.name
            binding.ivFavourite.setImageResource(
                if (item.isFavourite) {
                    R.drawable.ic_star_24
                } else {
                    R.drawable.ic_star_border_24
                }
            )
        }
    }
}
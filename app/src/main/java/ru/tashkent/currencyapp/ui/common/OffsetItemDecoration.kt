package ru.tashkent.currencyapp.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecoration(
    private val horizontalOffset: Int,
    private val outVerticalOffset: Int,
    private val inVerticalOffset: Int,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter ?: return
        val position = parent.getChildAdapterPosition(view)
        val isFirst = position == 0
        val isLast = position == adapter.itemCount - 1
        with(outRect) {
            left = horizontalOffset
            right = horizontalOffset
            top = if (isFirst) outVerticalOffset else inVerticalOffset
            bottom = if (isLast) outVerticalOffset else inVerticalOffset
        }
    }
}
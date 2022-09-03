package ru.tashkent.currencyapp.ui.common

import android.content.Context
import kotlin.math.roundToInt

object UiUtils {
    fun dpToPx(dp: Float, context: Context) =
        (dp * context.resources.displayMetrics.density).roundToInt()
}
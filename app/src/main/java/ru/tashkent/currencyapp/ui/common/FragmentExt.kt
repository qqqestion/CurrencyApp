package ru.tashkent.currencyapp.ui.common

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.tashkent.currencyapp.R

fun Fragment.snackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    onDismiss: () -> Unit = {}
) = Snackbar.make(requireView(), text, duration)
    .apply {
        anchorView = requireActivity().findViewById(R.id.bottom_navigation)
    }
    .addCallback(
        object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(
                transientBottomBar: Snackbar?,
                event: Int
            ) {
                super.onDismissed(transientBottomBar, event)
                onDismiss()
            }
        }
    )
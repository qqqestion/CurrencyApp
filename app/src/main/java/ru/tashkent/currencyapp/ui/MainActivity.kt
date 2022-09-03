package ru.tashkent.currencyapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuProvider
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.R
import ru.tashkent.currencyapp.data.SortOption
import ru.tashkent.currencyapp.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
            .also(binding.bottomNavigation::setupWithNavController)
        binding.bottomNavigation.setOnItemReselectedListener {}
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.setCurrentItem(destination.toDestination())
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon = AppCompatResources.getDrawable(this, R.drawable.ic_sort_24)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    supportActionBar?.title = state.currencySymbol
                    invalidateMenu()
                    val menuProvider = SortMenuProviderMenuProvider(state)
                    removeMenuProvider(menuProvider)
                    addMenuProvider(menuProvider)
                }
            }
        }
    }

    private fun NavDestination.toDestination() = when (id) {
        R.id.popularFragment -> BottomNavigationDestination.Popular
        R.id.favouriteFragment -> BottomNavigationDestination.Favourite
        R.id.settingsFragment -> BottomNavigationDestination.Settings
        else -> BottomNavigationDestination.NonBottomNavigation
    }

    private inner class SortMenuProviderMenuProvider(
        private val state: MainState
    ) : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            state.sortOptions.forEach { sortOption ->
                val title = when (sortOption) {
                    SortOption.ALPHABETICAL_ASCENDING -> R.string.alphabetically_ascending
                    SortOption.ALPHABETICAL_DESCENDING -> R.string.alphabetically_descending
                    SortOption.RATE_ASCENDING -> R.string.rate_ascending
                    SortOption.RATE_DESCENDING -> R.string.rate_descending
                }
                menu.add(Menu.NONE, title, Menu.NONE, title)?.isCheckable = true
            }
        }

        override fun onPrepareMenu(menu: Menu) {
            state.sortOptions.forEachIndexed { index, sortOption ->
                val menuItem = menu.getItem(index)
                menuItem.isVisible = state.isSortVisible
                menuItem.isChecked = state.currentSort == sortOption
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem) =
            when (menuItem.itemId) {
                R.string.alphabetically_ascending -> {
                    viewModel.setSort(SortOption.ALPHABETICAL_ASCENDING)
                    true
                }
                R.string.alphabetically_descending -> {
                    viewModel.setSort(SortOption.ALPHABETICAL_DESCENDING)
                    true
                }
                R.string.rate_ascending -> {
                    viewModel.setSort(SortOption.RATE_ASCENDING)
                    true
                }
                R.string.rate_descending -> {
                    viewModel.setSort(SortOption.RATE_DESCENDING)
                    true
                }
                else -> false
            }
    }
}

package com.hastakala.shop.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.hastakala.shop.R
import com.hastakala.shop.authentication.SignInActivity
import com.hastakala.shop.databinding.ActivityMainBinding
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocalizedActivity
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class MainActivity : LocalizedActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as HastaKalaApplication).salesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        applyTopBarInsets()

        binding.topAppBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_profile) {
                openProfile()
                true
            } else {
                false
            }
        }

        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment)
            ?: NavHostFragment.create(R.navigation.main_nav_graph).also { host ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mainNavHost, host)
                    .setPrimaryNavigationFragment(host)
                    .commitNow()
            }
        navController = navHostFragment.navController
        if (navController.currentDestination == null) {
            navController.setGraph(R.navigation.main_nav_graph)
        }
        binding.bottomNavigation.selectedItemId = navController.currentDestination?.id ?: R.id.homeFragment

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) return@setOnItemSelectedListener true
            navController.navigate(
                item.itemId,
                null,
                navOptions {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(R.id.homeFragment) {
                        saveState = true
                    }
                    anim {
                        enter = R.anim.nav_enter
                        exit = R.anim.nav_exit
                        popEnter = R.anim.nav_pop_enter
                        popExit = R.anim.nav_pop_exit
                    }
                }
            )
            true
        }

        binding.topAppBar.setNavigationOnClickListener {
            if (navController.currentDestination?.id == R.id.profileFragment) {
                navController.popBackStack()
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val onProfile = destination.id == R.id.profileFragment
            binding.bottomNavigation.isVisible = !onProfile
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
            invalidateOptionsMenu()
        }

        viewModel.events.observe(this) {
            Toast.makeText(this, localizedEvent(it), Toast.LENGTH_SHORT).show()
        }
    }

    private fun localizedEvent(message: String): String = when (message) {
        "Sale saved successfully" -> getString(R.string.sale_saved_successfully)
        "Unable to save sale" -> getString(R.string.unable_to_save_sale)
        "Stock added" -> getString(R.string.stock_added)
        "Stock updated" -> getString(R.string.stock_updated)
        "Stock removed" -> getString(R.string.stock_removed)
        else -> message
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_app_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_profile)?.isVisible =
            (supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment)
                ?.navController?.currentDestination?.id != R.id.profileFragment
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                openProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openProfile() {
        if (navController.currentDestination?.id != R.id.profileFragment) {
            navController.navigate(
                R.id.profileFragment,
                null,
                navOptions {
                    launchSingleTop = true
                    anim {
                        enter = R.anim.nav_enter
                        exit = R.anim.nav_exit
                        popEnter = R.anim.nav_pop_enter
                        popExit = R.anim.nav_pop_exit
                    }
                }
            )
        }
    }

    private fun applyTopBarInsets() {
        val baseHeight = resources.getDimensionPixelSize(R.dimen.top_app_bar_height)
        val start = binding.topAppBar.paddingStart
        val end = binding.topAppBar.paddingEnd
        val bottom = binding.topAppBar.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams {
                height = baseHeight + topInset
            }
            view.setPadding(start, topInset, end, bottom)
            insets
        }
    }

    fun logout() {
        (application as HastaKalaApplication).sessionManager.clearSession()
        startActivity(Intent(this, SignInActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.nav_pop_enter, R.anim.nav_exit)
    }
}

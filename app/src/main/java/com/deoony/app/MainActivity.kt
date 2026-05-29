package com.deoony.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deoony.app.data.database.AppDatabase
import com.deoony.app.data.repository.DebtRepository
import com.deoony.app.ui.screens.home.HomeScreen
import com.deoony.app.ui.theme.MyApplicationTheme
import com.deoony.app.ui.util.safeParseColor
import com.deoony.app.ui.viewmodel.DebtViewModel
import com.deoony.app.ui.viewmodel.DebtViewModelFactory
import com.deoony.app.ui.viewmodel.ThemePreference

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DebtViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup simple Room construction logic manually
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = DebtRepository(database.debtDao(), database.debtPaymentDao())
        val factory = DebtViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DebtViewModel::class.java]

        val prefs = applicationContext.getSharedPreferences("DeyoniPrefs", android.content.Context.MODE_PRIVATE)
        val currentVersionCode = try { 
            packageManager.getPackageInfo(packageName, 0).versionCode 
        } catch (e: Exception) { 1 }
        val savedVersionCode = prefs.getInt("version_code", -1)
        val showWelcomeInitially = savedVersionCode != currentVersionCode
        if (showWelcomeInitially) {
            prefs.edit().putInt("version_code", currentVersionCode).apply()
        }

        enableEdgeToEdge()
        setContent {
            val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
            val bgHex by viewModel.backgroundHex.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themePref) {
                ThemePreference.SYSTEM -> systemDark
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                // Ensure correct RTL Arabic presentation layout direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val actualBackground = if (!darkTheme && bgHex != null) safeParseColor(bgHex!!) else MaterialTheme.colorScheme.background
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = actualBackground
                    ) { innerPadding ->
                        HomeScreen(
                            viewModel = viewModel,
                            showWelcomeInitially = showWelcomeInitially,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

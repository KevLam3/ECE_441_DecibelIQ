package com.example.ece441project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ece441project.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(app: Application) : AndroidViewModel(app) {

    val isDarkMode = ThemePreferences.isDarkMode(app)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            ThemePreferences.setDarkMode(getApplication(), enabled)
        }
    }

    val fontSize = ThemePreferences.fontSize(app)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun updateFontSize(size: Int) {
        viewModelScope.launch {
            ThemePreferences.setFontSize(getApplication(), size)
        }
    }

    val buttonColor = ThemePreferences.buttonColor(app)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0xFF6750A4.toInt())

    fun updateButtonColor(color: Int) {
        viewModelScope.launch {
            ThemePreferences.setButtonColor(getApplication(), color)
        }
    }
}
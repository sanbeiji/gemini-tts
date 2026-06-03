package com.example.geminittsdialects.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geminittsdialects.api.DialectRepository
import com.example.geminittsdialects.data.SettingsRepository

class DialectViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DialectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DialectViewModel(
                settingsRepository = SettingsRepository(context),
                dialectRepository = DialectRepository(),
                context = context.applicationContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

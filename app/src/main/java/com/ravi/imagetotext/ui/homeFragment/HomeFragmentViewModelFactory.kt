package com.ravi.imagetotext.ui.homeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ravi.imagetotext.data.DataDao

class HomeFragmentViewModelFactory(
    private val dataSource: DataDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


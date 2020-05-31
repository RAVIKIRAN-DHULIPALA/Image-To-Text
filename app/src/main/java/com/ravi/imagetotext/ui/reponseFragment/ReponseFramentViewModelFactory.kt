package com.ravi.imagetotext.ui.reponseFragment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.databinding.ResponseFramentFragmentBinding

class ResponseFramentViewModelFactory(
    private val activity: Activity,
    private val dataSource: DataDao,
    private val binding: ResponseFramentFragmentBinding
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResponseFramentViewModel::class.java)) {
            return ResponseFramentViewModel(activity, dataSource,binding) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
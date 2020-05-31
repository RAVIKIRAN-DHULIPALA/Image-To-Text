package com.ravi.imagetotext.ui.reponseFragment

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.data.User
import com.ravi.imagetotext.databinding.ResponseFramentFragmentBinding
import kotlinx.coroutines.*

class ResponseFramentViewModel (val activity: Activity, val database: DataDao, binding: ResponseFramentFragmentBinding) : ViewModel() {
    private var viewModelJob = Job()
    private var _flags  =  MutableLiveData<Int>()
    val flags :LiveData<Int>
        get() = _flags

    private var _file = MutableLiveData<User>()
    val file :LiveData<User>
        get() = _file
    lateinit var text:String
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    init {
        _flags.value = 0
    }
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val tv: TextView = binding.responseText
    private val tl: TextInputLayout = binding.editResponse
    private val tiet: TextInputEditText = binding.editResponseEditText

    fun getFile(id:Long){
        uiScope.launch {
            _file.value = getParticularFile(id)
        }
    }

    fun updateUser(user: User){
        uiScope.launch {
            update(user)
            _file.value = user
        }
    }
    private suspend fun update(user: User){
        withContext(Dispatchers.IO){
            database.update(user)
        }
    }
    private suspend fun getParticularFile(id:Long): User {
        return withContext(Dispatchers.IO){
            database.getAFile(id)
        }
    }
    fun copyText() {
        val content = tv.text.toString()
        val cpm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(content != "") {
            val clipData = ClipData.newPlainText("extracted_data", content)
            cpm.setPrimaryClip(clipData)
            Toast.makeText(activity.applicationContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(activity.applicationContext, "Nothing to copy", Toast.LENGTH_SHORT).show()
    }
    fun shareContent() {
        text = if (_flags.value == 0) {
            tv.text.toString()
        } else {
            tiet.text.toString()
        }
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, "Share Using")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.applicationContext.startActivity(shareIntent)
    }

    fun edit(){
        _flags.value = if (_flags.value == 0) {
            val content = tv.text.toString()
            tv.visibility = View.GONE
            tl.visibility = View.VISIBLE
            tiet.setText(content)
            1
        } else {
            val content = tiet.text.toString()
            tv.visibility = View.VISIBLE
            tv.text = content
            tl.visibility = View.GONE
            0
        }
    }


}

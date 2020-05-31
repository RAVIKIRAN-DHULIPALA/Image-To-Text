package com.ravi.imagetotext.ui.homeFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.data.User
import com.ravi.imagetotext.utils.Utils
import com.ravi.imagetotext.R
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class HomeViewModel(val database: DataDao) : ViewModel() {
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight  =  MutableLiveData<User?>()
    val nights = database.getAllFiles()

    private val _navigateToResponse = MutableLiveData<User?>()
    val navigateToReponse :LiveData<User?>
        get() = _navigateToResponse

    fun doneNavigation(){
        _navigateToResponse.value = null
    }

    fun insertUser(user: User){
        uiScope.launch {
            insert(user)
            _navigateToResponse.value = user
        }
    }
    private suspend fun insert(user: User){
        withContext(Dispatchers.IO){
            database.insertData(user)

        }
    }

    private fun getCloudDocumentRecognizer(): FirebaseVisionDocumentTextRecognizer {
        val options =
            FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                .setLanguageHints(
                    listOf(
                        "en",
                        "hi",
                        "kn",
                        "mr",
                        "pa",
                        "ta",
                        "te"
                    )
                )
                .build()
        return FirebaseVision.getInstance()
            .getCloudDocumentTextRecognizer(options)
    }

    fun processCloudDocumentImage(
        image: FirebaseVisionImage,
        bitmap: Bitmap,
        context: Context,
        show: AlertDialog
    ) {
        val detector = getCloudDocumentRecognizer()
        detector.processImage(image)
            .addOnSuccessListener {
                processDocumentTextBlock(it,bitmap,context,show)
            }
            .addOnFailureListener {
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun processDocumentTextBlock(
        result: FirebaseVisionDocumentText,
        bitmap: Bitmap,
        context: Context,
        show: AlertDialog
    ) {
        val resultText = result.text
        if(resultText == ""){
            val activity = context as Activity
            show.dismiss()
            val snackbar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                "No text found",
                Snackbar.LENGTH_LONG
            )
            snackbar.show()

        } else {
            val fileId = Random.nextLong(0, 100000)

            val timeStampFormat = SimpleDateFormat("yyyyMMddHHmmss")
            val myDate = Date()
            val filename: String = timeStampFormat.format(myDate)

            val timeStampFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val myDate1 = Date()
            val create_date: String = timeStampFormat1.format(myDate1)

            val byteArray = Utils.bitmapToByteArray(bitmap)
            val user = User(fileId,filename,resultText,"",create_date,"",false,create_date,byteArray, R.drawable.unknown)

            show.dismiss()
            insertUser(user)
        }
    }

}

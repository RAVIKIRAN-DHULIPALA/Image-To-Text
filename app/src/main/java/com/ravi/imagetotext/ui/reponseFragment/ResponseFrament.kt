package com.ravi.imagetotext.ui.reponseFragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.data.User
import com.ravi.imagetotext.data.UserDatabase
import com.ravi.imagetotext.R
import com.ravi.imagetotext.databinding.ResponseFramentFragmentBinding
import com.ravi.imagetotext.utils.Utils
import kotlinx.android.synthetic.main.popupimage_layout.view.*
import java.text.SimpleDateFormat
import java.util.*


class ResponseFrament : Fragment() {
    companion object {
        fun newInstance() = ResponseFrament()
    }
    lateinit var text :String
    lateinit var createDate :String
    lateinit var viewModel: ResponseFramentViewModel
    lateinit var user: User
    var fileId:Long =0
    var flag = 0
    var texts = ""
    var f= 0
    lateinit var dataSource: DataDao
    lateinit var b:MaterialAlertDialogBuilder
    lateinit var c : LayoutInflater

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding  = DataBindingUtil.inflate<ResponseFramentFragmentBinding>(inflater,
            R.layout.response_frament_fragment,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.bottomAppBar)
        (activity as AppCompatActivity).supportStartPostponedEnterTransition()
        c= inflater
        val application = requireNotNull(this.activity).application
        fileId = ResponseFramentArgs.fromBundle(arguments!!).fileId
        dataSource = UserDatabase.getInstance(application).userDatabaseDao()
        val responseFramentViewModelFactory = ResponseFramentViewModelFactory(context as Activity,dataSource, binding)
        viewModel = ViewModelProvider(this,responseFramentViewModelFactory).get(
            ResponseFramentViewModel::class.java)
        binding.responseF = viewModel
        binding.responseText.movementMethod = ScrollingMovementMethod()
        viewModel.getFile(fileId)
        viewModel.file.observe(viewLifecycleOwner, Observer {
            binding.responseText.text = it.file_content
            binding.fileTitle.text = it.file_name
            createDate = it.create_date
            text = it.file_content
            user = it

        })
        binding.editResponseEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val timeStampFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val myDate1 = Date()
                val modifdate: String = timeStampFormat1.format(myDate1)

                if (user.is_saved) {
                    flag = 1
                    texts = s.toString()
                    val userd= User(fileId, user.file_name, s.toString(), user.file_ext, user.create_date, user.file_path, user.is_saved, modifdate,user.byteArray,user.file_icon)
                    viewModel.updateUser(userd)
                }
                else{
                    texts = s.toString()
                    val userd= User(fileId, user.file_name, s.toString(), "", user.create_date, "", false, modifdate,user.byteArray,user.file_icon)
                    viewModel.updateUser(userd)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        viewModel.flags.observe(viewLifecycleOwner, Observer {
            text = if( it == 0 ){
                binding.responseText.text.toString()

            } else{
                binding.editResponseEditText.text.toString()
            }

        }
        )
        binding.editItem.setOnClickListener {
            showImage()
        }

        binding.navigateUp.setOnClickListener {
            navigateUp()
        }

        return binding.root
    }
    private fun navigateUp(){
            findNavController()
                .navigate(ResponseFramentDirections.actionResponseFramentToHomeFragment2())
    }
    fun showImage(){
        val cl = c.inflate(R.layout.popupimage_layout,null)
        val b = Utils.byteArrayToBitmap(user.byteArray)
        cl.popup.setImageBitmap(b)
        val settingsDialog = Dialog(this.activity!!)
        settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        settingsDialog.setContentView(cl)
        settingsDialog.show()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                findNavController()
                    .navigate(ResponseFramentDirections.actionResponseFramentToHomeFragment2())
        }
        callback.isEnabled = true
    }


}

package com.ravi.imagetotext.ui.homeFragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.ravi.imagetotext.R
import com.ravi.imagetotext.adapter.MyAdapter
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.data.UserDatabase
import com.ravi.imagetotext.databinding.HomeFragmentBinding
import com.ravi.imagetotext.utils.ProductGridItemDecoration
import kotlinx.android.synthetic.main.home_fragment.*


@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    lateinit var adapter: MyAdapter
    lateinit var binding: HomeFragmentBinding
    lateinit var dialog:MaterialAlertDialogBuilder
    lateinit var viewModel: HomeViewModel
    lateinit var dataSource: DataDao


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.home_fragment,container,false)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        val application = requireNotNull(this.activity).application
        dataSource = UserDatabase.getInstance(application).userDatabaseDao()
        val viewModelFactory = HomeFragmentViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        val recyclerView = binding.recycler
        recyclerView.layoutManager = GridLayoutManager(context,2)
        val largePadding = resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small)
        recyclerView.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))
        adapter =  MyAdapter()
        recyclerView.adapter = adapter
        viewModel.nights.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if(it.isNotEmpty()){
                    adapter.submitList(it)
                    adapter.items = it
                    binding.noitems.visibility = View.GONE
                    binding.recycler.visibility = View.VISIBLE
                }
                else{
                    adapter.submitList(emptyList())
                    binding.noitems.visibility = View.VISIBLE
                    binding.recycler.visibility = View.GONE
                }
            }
        })

        binding.floatingActionButton.setOnClickListener {
            openPicker()
        }
        dialog = MaterialAlertDialogBuilder(context)
        dialog.setView(R.layout.loading_dialog)
        dialog.create()
        viewModel.navigateToReponse.observe(viewLifecycleOwner, Observer {user->
            user?.let {
                this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToResponseFrament2(user.file_id))
                viewModel.doneNavigation()
            }
        })


        binding.lifecycleOwner = this
        return binding.root
    }


    private fun openPicker() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri = data?.data
                val show = dialog.show()
                val bitmap = BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(fileUri!!))
                context?.let {
                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    viewModel.processCloudDocumentImage(image,bitmap,it,show)
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

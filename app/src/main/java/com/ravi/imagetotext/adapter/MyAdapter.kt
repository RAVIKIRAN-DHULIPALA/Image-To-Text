package com.ravi.imagetotext.adapter

import android.view.*
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ravi.imagetotext.data.User
import com.ravi.imagetotext.R
import com.ravi.imagetotext.ui.homeFragment.HomeFragmentDirections
import com.ravi.imagetotext.viewholders.MyViewHolder

class MyAdapter()
    : ListAdapter<User, MyViewHolder>(UserDiffCallback()){
    var items= listOf<User>()
    set(value) {
        field = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_list,parent,false)
        return MyViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val iitem = getItem(position)
        holder.bind(iitem)
        val itemView = holder.itemView
        holder.itemView.setOnClickListener {
                findNavController(it).navigate(
                    HomeFragmentDirections.actionHomeFragmentToResponseFrament2(
                        iitem.file_id
                    )
                )
            }
        }
    }

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.file_id == newItem.file_id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem ==  newItem
    }
}


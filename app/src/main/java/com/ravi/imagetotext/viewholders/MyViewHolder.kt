package com.ravi.imagetotext.viewholders

import android.app.Activity
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.ravi.imagetotext.data.User
import com.ravi.imagetotext.utils.Utils
import kotlinx.android.synthetic.main.recycler_list.view.*

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    lateinit var activity: Activity

    lateinit var context:Context

    fun bind(user: User){
        itemView.fileName.text = user.file_name
        itemView.thumbnail.setImageBitmap(Utils.byteArrayToBitmap(user.byteArray))
        itemView.fileIcon.setImageResource(user.file_icon)

    }
}
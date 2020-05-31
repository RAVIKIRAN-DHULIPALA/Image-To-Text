package com.ravi.docextract.adapters

import android.app.Activity
import android.content.Context
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ravi.docextract.R
import com.ravi.imagetotext.data.DataDao
import com.ravi.imagetotext.data.User
import com.ravi.docextract.ui.homeFragment.HomeFragmentDirections
import com.ravi.docextract.utils.RoundedBottomSheetDialogFragment
import com.ravi.docextract.viewholders.MyViewHolder
import com.ravi.imagetotext.viewholders.MyViewHolder
import kotlinx.android.synthetic.main.recycler_list.view.*

class MyAdapter(
    private val context: Context,
    private val fragman: FragmentManager,
    private val dataSource: DataDao
)
    : ListAdapter<User, MyViewHolder>(UserDiffCallback()) ,ActionMode.Callback{
    var items= listOf<User>()
    set(value) {
        field = value
    }
    private var multiSelect = false
    private val selectedItems =arrayListOf<Pair<User,View>>()
    var actionmode:ActionMode? = null
    private var isSelectAll = false
    private val views= arrayListOf<View>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_list,parent,false)
        return MyViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val iitem = getItem(position)
        holder.bind(iitem)
        val itemView = holder.itemView
        if(!views.contains(itemView))
            views.add(itemView)
        holder.itemView.card.isChecked = isSelectAll
        holder.itemView.setOnClickListener {
            if (multiSelect) {
                selectItem(iitem, it)
                updated()
            }
            else {
                findNavController(it).navigate(
                    HomeFragmentDirections.actionHomeFragmentToResponseFrament2(
                        iitem.file_id
                    )
                )
            }
        }
        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                actionmode = (context as Activity).startActionMode(this)
                selectItem(iitem,it)
                updated()
                true
            }
            else {
                false
            }
        }
        holder.itemView.more_action.setOnClickListener {
                val sheet = RoundedBottomSheetDialogFragment(iitem)
                sheet.show(fragman, sheet.tag)
            }
    }
    private fun updated(){
        if (selectedItems.size ==1)
            actionmode?.title = "${selectedItems.size} item"
        else if (selectedItems.size >1)
            actionmode?.title = "${selectedItems.size} items"
    }
    private fun selectItem(user: User, it: View) {
        if(multiSelect) {
            if (selectedItems.contains(Pair(user,it))) {
                selectedItems.remove(Pair(user,it))
                it.card.isChecked = !it.card.isChecked
                it.more_action.visibility = View.VISIBLE
                if(selectedItems.size == 0){
                    actionmode?.finish()
                    it.more_action.visibility = View.VISIBLE
                }
            } else {
                // Else, add it to the list and add a darker shade over the image, letting the user know that it was selected
                selectedItems.add(Pair(user,it))
                it.card.isChecked = !it.card.isChecked
                it.more_action.visibility = View.GONE
            }
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete) {
            for (ite in selectedItems){
                HomeViewModel(dataSource).deleteUser(ite.first)
            }
            mode?.finish()
        }
        if(item?.itemId == R.id.action_selectall){
            for(it in items.indices){
                val pair = Pair(items[it], views[it])
                if(!selectedItems.contains(pair))
                    selectedItems.add(pair)
            }
            updated()
            isSelectAll = true
            notifyDataSetChanged()
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater: MenuInflater = mode!!.menuInflater
        inflater.inflate(R.menu.menu_action, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu?.findItem(R.id.action_delete)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        multiSelect = false
        for (ite in selectedItems){
            ite.second.card.isChecked = !ite.second.card.isChecked
            ite.second.more_action.visibility = View.VISIBLE
        }
        isSelectAll = false
        selectedItems.clear()
        notifyDataSetChanged()
        mode?.finish()
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


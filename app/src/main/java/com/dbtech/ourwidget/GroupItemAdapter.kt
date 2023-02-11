package com.dbtech.ourwidget

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class GroupItemAdapter : RecyclerView.Adapter<GroupItemAdapter.GroupItemViewHolder>() {
    var data = listOf<Group>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GroupItemViewHolder = GroupItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: GroupItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }




    class GroupItemViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val groupName = rootView.findViewById<TextView>(R.id.group_name)
        companion object {
            fun inflateFrom(parent: ViewGroup) : GroupItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.group_item, parent, false) as CardView
                return GroupItemViewHolder(view)
            }
        }
        fun bind(item: Group) {
            groupName.text = item.name
            rootView.setOnClickListener { view ->
                val action = MyGroupsFragmentDirections.actionMyGroupsFragmentToGroupFragment(item.id)
                view.findNavController().navigate(action)
            }
        }
    }




}
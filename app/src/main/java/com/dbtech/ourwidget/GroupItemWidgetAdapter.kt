package com.dbtech.ourwidget

import android.appwidget.AppWidgetManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dbtech.ourwidget.PhotoWidgetConfigureActivity

class GroupItemWidetAdapter : RecyclerView.Adapter<GroupItemWidetAdapter.GroupItemWidgetViewHolder>() {
    var data = listOf<Group>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GroupItemWidgetViewHolder = GroupItemWidgetViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: GroupItemWidgetViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }




    class GroupItemWidgetViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val groupName = rootView.findViewById<TextView>(R.id.group_name)
        companion object {
            fun inflateFrom(parent: ViewGroup) : GroupItemWidgetViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.group_item, parent, false) as CardView
                return GroupItemWidgetViewHolder(view)
            }
        }
        fun bind(item: Group) {
            groupName.text = item.name
            rootView.setOnClickListener { view ->
                item.onWidgetContainerClicked(item.id)
            }
        }
    }




}
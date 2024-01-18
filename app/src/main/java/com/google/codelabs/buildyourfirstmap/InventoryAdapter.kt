package com.google.codelabs.buildyourfirstmap
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.codelabs.buildyourfirstmap.classes.GameItem

class InventoryAdapter(context: Context, resource: Int, objects: List<GameItem>) :
    ArrayAdapter<GameItem>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false)

        val item = getItem(position)

        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)

        itemNameTextView.text = item?.name

        return itemView
    }
}

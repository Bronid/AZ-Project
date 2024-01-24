package com.google.codelabs.buildyourfirstmap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.codelabs.buildyourfirstmap.classes.GameItem
import com.google.codelabs.buildyourfirstmap.classes.GameItemHeal
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter

class InventoryAdapter(
    context: Context,
    resource: Int,
    objects: List<GameItem>,
    private val playerCharacter: PlayerCharacter?,
    private val callback: InventoryAdapterCallback
) : ArrayAdapter<GameItem>(context, resource, objects) {

    interface InventoryAdapterCallback {
        fun onItemUsed(healAmount: Int, removedItem: GameItem?)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false)

        val item = getItem(position)

        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)

        itemNameTextView.text = item?.name

        // Добавьте обработчик клика на элемент списка
        itemView.setOnClickListener {
            if (item is GameItemHeal) {
                val healAmount = item.heal()
                playerCharacter?.changeHealth(healAmount)

                // Удаление предмета из инвентаря
                remove(item)
                playerCharacter?.inventory?.remove(item)

                // Обновление интерфейса
                notifyDataSetChanged()

                // Уведомление об использовании предмета через callback
                callback.onItemUsed(healAmount, item)
            }
        }

        return itemView
    }
}


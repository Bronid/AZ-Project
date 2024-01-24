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
import com.google.codelabs.buildyourfirstmap.classes.GameItemArmor
import com.google.codelabs.buildyourfirstmap.classes.GameItemHeal
import com.google.codelabs.buildyourfirstmap.classes.GameItemWeapon
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
        fun onWeaponEquipped(weapon: GameItemWeapon?)
        fun onArmorEquipped(armor: GameItemArmor?)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false)

        val item = getItem(position)

        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)

        itemNameTextView.text = item?.name

        itemView.setOnClickListener {
            when (item) {
                is GameItemHeal -> {
                    val healAmount = item.heal()
                    playerCharacter?.changeHealth(healAmount)
                    playerCharacter?.inventory?.remove(item)
                    notifyDataSetChanged()
                    callback.onItemUsed(healAmount, item)
                }
                is GameItemWeapon -> {
                    playerCharacter?.weapon = item
                    playerCharacter?.inventory?.remove(item)
                    notifyDataSetChanged()
                    callback.onWeaponEquipped(item)
                }
                is GameItemArmor -> {
                    playerCharacter?.armor = item
                    playerCharacter?.inventory?.remove(item)
                    notifyDataSetChanged()
                    callback.onArmorEquipped(item)
                }
            }
        }

        return itemView
    }
}


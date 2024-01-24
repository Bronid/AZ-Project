package com.google.codelabs.buildyourfirstmap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.GameItem
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter

class InventoryActivity : AppCompatActivity(), InventoryAdapter.InventoryAdapterCallback {

    private lateinit var listView: ListView
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        supportActionBar?.hide()

        val playerCharacter: PlayerCharacter? = intent.getSerializableExtra("playerCharacter") as? PlayerCharacter
        val playerInventory: List<GameItem> = playerCharacter?.inventory ?: emptyList()
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        listView = findViewById(R.id.listView)
        inventoryAdapter = InventoryAdapter(this, R.layout.item_inventory, playerInventory, playerCharacter, this)

        listView.adapter = inventoryAdapter
    }

    override fun onItemUsed(healAmount: Int, removedItem: GameItem?) {
        // Вызывается при использовании предмета в адаптере
        val resultIntent = Intent()
        resultIntent.putExtra("healAmount", healAmount)
        resultIntent.putExtra("removedItem", removedItem)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

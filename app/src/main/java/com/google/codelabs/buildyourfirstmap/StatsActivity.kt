package com.google.codelabs.buildyourfirstmap
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Assuming you have a TextView in your layout with the id "statsTextView"
        val statsTextView: TextView = findViewById(R.id.statsTextView)
        val backButton: Button = findViewById(R.id.backButton)

        // Set up back button click listener
        backButton.setOnClickListener {
            finish() // Close the activity when the "Back" button is clicked
        }
        // Assuming you passed the PlayerCharacter object to this activity through Intent
        val playerCharacter: PlayerCharacter? = intent.getSerializableExtra("playerCharacter") as? PlayerCharacter

        playerCharacter?.let {
            val statsText = buildStatsText(it)
            statsTextView.text = statsText
        }



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the activity when the "Back" button is pressed
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildStatsText(playerCharacter: PlayerCharacter): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("User Login: ${playerCharacter.userLogin ?: "null"}\n")
        stringBuilder.append("Nickname: ${playerCharacter.nickname ?: "null"}\n")
        stringBuilder.append("Description: ${playerCharacter.description ?: "null"}\n")
        stringBuilder.append("Current Experience: ${playerCharacter.currentExperience}\n")
        stringBuilder.append("Inventory: ${playerCharacter.inventory ?: "null"}\n")
        stringBuilder.append("Armor: ${playerCharacter.armor ?: "null"}\n")
        stringBuilder.append("Weapon: ${playerCharacter.weapon ?: "null"}\n")
        stringBuilder.append("Skill Points: ${playerCharacter.skillPoints}\n")
        stringBuilder.append("Strength: ${playerCharacter.strength}\n")
        stringBuilder.append("Agility: ${playerCharacter.agility}\n")
        stringBuilder.append("Constitution: ${playerCharacter.constitution}\n")
        stringBuilder.append("Health: ${playerCharacter.getCurrentHealth()}\n")
        stringBuilder.append("Is Knocked: ${playerCharacter.isKnocked()}\n")
        stringBuilder.append("Level: ${playerCharacter.level}\n")
        stringBuilder.append("Damage: ${playerCharacter.damage.map { it.roll() }}\n")

        return stringBuilder.toString()
    }
}

package com.google.codelabs.buildyourfirstmap
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter
import kotlinx.android.synthetic.main.activity_stats.skillsStats

class StatsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        supportActionBar?.hide()

        // Assuming you have a TextView in your layout with the id "statsTextView"
        val basicTextView: TextView = findViewById(R.id.basicStats)
        val SkillsTextView: TextView = findViewById(R.id.skillsStats)
        val EquipTextView: TextView = findViewById(R.id.equipStats)
        val backButton: Button = findViewById(R.id.backButton)

        // Set up back button click listener
        backButton.setOnClickListener {
            finish() // Close the activity when the "Back" button is clicked
        }
        // Assuming you passed the PlayerCharacter object to this activity through Intent
        val playerCharacter: PlayerCharacter? = intent.getSerializableExtra("playerCharacter") as? PlayerCharacter

        playerCharacter?.let {
            val basicText = buildStatsText(it)
            val skillsText = buildSkillsText(it)
            val equipText = buildEquipText(it)

            basicTextView.text = basicText
            SkillsTextView.text = skillsText
            EquipTextView.text = equipText

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

        stringBuilder.append("Nickname: ${playerCharacter.nickname ?: "null"}\n")
        stringBuilder.append("Health: ${playerCharacter.currentHealth}\n")
        stringBuilder.append("Current Experience: ${playerCharacter.currentExperience}\n")
        stringBuilder.append("Level: ${playerCharacter.level}\n")


        return stringBuilder.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildEquipText(playerCharacter: PlayerCharacter): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("Damage: ${playerCharacter.damageToString()}\n")
        stringBuilder.append("Armor: ${playerCharacter.armor ?: "null"}\n")
        stringBuilder.append("Weapon: ${playerCharacter.weapon ?: "null"}\n")

        return stringBuilder.toString()
    }

    private fun buildSkillsText(playerCharacter: PlayerCharacter): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("Strength: ${playerCharacter.strength}\n")
        stringBuilder.append("Agility: ${playerCharacter.agility}\n")
        stringBuilder.append("Constitution: ${playerCharacter.constitution}\n")

        return stringBuilder.toString()
    }
}


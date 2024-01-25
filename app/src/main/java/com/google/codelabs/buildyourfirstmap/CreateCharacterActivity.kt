package com.google.codelabs.buildyourfirstmap

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter
import com.google.codelabs.buildyourfirstmap.classes.User
import com.google.codelabs.buildyourfirstmap.database.MongoDBManager

class CreateCharacterActivity : AppCompatActivity() {
    private lateinit var nicknameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var strengthSeekBar: SeekBar
    private lateinit var agilitySeekBar: SeekBar
    private lateinit var constitutionSeekBar: SeekBar
    private lateinit var pointsRemainingTextView: TextView
    private lateinit var createCharacterButton: Button

    private var remainingPoints = 5

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                val totalProgress =
                    strengthSeekBar.progress + agilitySeekBar.progress + constitutionSeekBar.progress

                if (totalProgress > 5) {
                    // If the total progress exceeds the limit, prevent the change
                    seekBar?.progress = (seekBar?.progress ?: 0) - (totalProgress - 5)
                }

                updatePointsRemaining()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // Nothing to do
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // Nothing to do
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_character)
        val user = intent.getSerializableExtra("user") as User

        supportActionBar?.hide()

        nicknameEditText = findViewById(R.id.editTextNickname)
        descriptionEditText = findViewById(R.id.editTextDescription)
        strengthSeekBar = findViewById(R.id.seekBarStrength)
        agilitySeekBar = findViewById(R.id.seekBarAgility)
        constitutionSeekBar = findViewById(R.id.seekBarConstitution)
        pointsRemainingTextView = findViewById(R.id.textViewPointsRemaining)
        createCharacterButton = findViewById(R.id.buttonCreateCharacter)

        updatePointsRemaining()

        strengthSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        agilitySeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        constitutionSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)

        createCharacterButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val strength = strengthSeekBar.progress
            val agility = agilitySeekBar.progress
            val constitution = constitutionSeekBar.progress

            if (nickname.isEmpty() || description.isEmpty()) {
                // Handle the case where fields are not filled
                return@setOnClickListener
            }

            if (remainingPoints != 0) {
                // Handle the case where not all points are distributed
                return@setOnClickListener
            }

            // Execute AsyncTask for database operation
            SaveCharacterTask().execute(user, nickname, description, strength, agility, constitution)
        }
    }

    private fun updatePointsRemaining() {
        remainingPoints =
            5 - (strengthSeekBar.progress + agilitySeekBar.progress + constitutionSeekBar.progress)

        if (remainingPoints < 0) {
            createCharacterButton.isEnabled = false
            pointsRemainingTextView.text = getString(R.string.points_remaining_exceeded)
        } else {
            createCharacterButton.isEnabled = true
            pointsRemainingTextView.text = getString(R.string.points_remaining, remainingPoints)
        }
    }

    private inner class SaveCharacterTask : AsyncTask<Any, Void, PlayerCharacter>() {
        override fun doInBackground(vararg params: Any?): PlayerCharacter {
            val user = params[0] as User
            val nickname = params[1] as String
            val description = params[2] as String
            val strength = params[3] as Int
            val agility = params[4] as Int
            val constitution = params[5] as Int

            val character = PlayerCharacter(
                user.login, nickname, description, 0, (5 + constitution) + 2,
                false, mutableListOf(), null, null, 0, strength, agility, constitution
            )

            val mongoDBManager = MongoDBManager()
            mongoDBManager.addOrUpdatePlayerCharacter(character)
            mongoDBManager.closeConnection()

            return character
        }

        override fun onPostExecute(result: PlayerCharacter) {
            finish()
        }
    }
}

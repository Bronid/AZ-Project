package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.User
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter
import com.google.codelabs.buildyourfirstmap.database.MongoDBManager

class MainActivity : AppCompatActivity() {

    lateinit var activeUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val userLogin: EditText = findViewById(R.id.editTextLogin)
        val userPassword: EditText = findViewById(R.id.editTextPassword)
        val button: Button = findViewById(R.id.buttonLogin)
        val linkToRegister: TextView = findViewById(R.id.textViewNeedAccount)

        linkToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
            } else {
                // Execute AsyncTask for login
                LoginAsyncTask().execute(login, password)
            }
        }
    }

    private inner class LoginAsyncTask : AsyncTask<String, Void, User?>() {

        override fun doInBackground(vararg params: String): User? {
            val login = params[0]
            val password = params[1]

            val mongoDBManager = MongoDBManager()
            return mongoDBManager.getUserByLoginAndPassword(login, password)
        }

        override fun onPostExecute(result: User?) {
            if (result != null) {
                Toast.makeText(applicationContext, "User logged", Toast.LENGTH_LONG).show()
                activeUser = result
                // Execute AsyncTask for retrieving character information
                GetCharacterAsyncTask().execute(result.login)
            } else {
                Toast.makeText(applicationContext, "Login or password incorrect", Toast.LENGTH_LONG).show()
            }
        }
    }

    private inner class GetCharacterAsyncTask : AsyncTask<String, Void, PlayerCharacter?>() {

        override fun doInBackground(vararg params: String): PlayerCharacter? {
            val userLogin = params[0]

            val mongoDBManager = MongoDBManager()
            return mongoDBManager.getPlayerCharacterByUserLogin(userLogin)
        }

        override fun onPostExecute(character: PlayerCharacter?) {
            if (character == null) {
                val intent = Intent(this@MainActivity, CreateCharacterActivity::class.java)
                intent.putExtra("user", activeUser) // "user" - key for passing the User object
                startActivity(intent)
            } else {
                // Proceed to the next activity and pass the User and Character objects
                val intent = Intent(this@MainActivity, MapActivity::class.java)
                intent.putExtra("user", activeUser)
                intent.putExtra("character", character)
                startActivity(intent)
            }
        }
    }
}

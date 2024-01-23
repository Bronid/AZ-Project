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
import com.google.codelabs.buildyourfirstmap.database.MongoDBManager

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        supportActionBar?.hide()

        val userLogin: EditText = findViewById(R.id.editTextLogin)
        val userPassword: EditText = findViewById(R.id.editTextPassword)
        val userRepeatPassword: EditText = findViewById(R.id.editTextRepeatPassword)
        val button: Button = findViewById(R.id.buttonRegister)
        val linkToLogin: TextView = findViewById(R.id.textViewAlreadyRegistered)

        linkToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val repeatPassword = userRepeatPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
            } else if (password != repeatPassword) {
                Toast.makeText(this, "Password repeat incorrect", Toast.LENGTH_LONG).show()
            } else {
                // Вызываем асинхронную задачу для выполнения запроса к базе данных
                RegisterUserAsyncTask().execute(Pair(login, password))
            }
        }
    }

    private inner class RegisterUserAsyncTask : AsyncTask<Pair<String, String>, Void, Unit>() {

        override fun doInBackground(vararg params: Pair<String, String>) {
            val login = params[0].first
            val password = params[0].second

            val mongoDBManager = MongoDBManager()
            val user = User(login, password)
            mongoDBManager.addOrUpdateUser(user)
            mongoDBManager.closeConnection()
        }

        override fun onPostExecute(result: Unit?) {
            Toast.makeText(applicationContext, "User registered", Toast.LENGTH_LONG).show()
        }
    }
}

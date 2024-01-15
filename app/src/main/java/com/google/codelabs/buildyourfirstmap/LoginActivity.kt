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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

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
                // Вызываем асинхронную задачу для выполнения запроса к базе данных
                AuthAsyncTask().execute(login, password)
            }
        }
    }

    private inner class AuthAsyncTask : AsyncTask<String, Void, User?>() {

        override fun doInBackground(vararg params: String): User? {
            val login = params[0]
            val password = params[1]

            val mongoDBManager = MongoDBManager()
            return mongoDBManager.getUserByLoginAndPassword(login, password)
        }

        override fun onPostExecute(result: User?) {
            if (result != null) {
                Toast.makeText(applicationContext, "User logged", Toast.LENGTH_LONG).show()
                // TODO: В этом месте вы можете перейти на другую активность, представляющую ваше основное приложение
            } else {
                Toast.makeText(applicationContext, "Login or password incorrect", Toast.LENGTH_LONG).show()
            }
        }
    }
}
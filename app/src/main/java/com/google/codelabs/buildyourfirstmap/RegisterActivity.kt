package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.classes.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val userLogin: EditText = findViewById(R.id.editTextLogin)
        val userPassword: EditText = findViewById(R.id.editTextPassword)
        val userRepeatPassword: EditText = findViewById(R.id.editTextRepeatPassword)
        val button: Button = findViewById(R.id.buttonRegister)
        val linkToLogin: TextView = findViewById(R.id.textViewAlreadyRegistered)

        linkToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener{
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val repeatPassword = userRepeatPassword.text.toString().trim()

            if(login == "" || password == "" || repeatPassword == "") {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
            }
            else if(password != repeatPassword){
                Toast.makeText(this, "Password repeat incorrect", Toast.LENGTH_LONG).show()
            }
            else {
                val user = User(login, password)
                val db = DbHelper(this, null)
                db.addUser(user)
                Toast.makeText(this, "User registered", Toast.LENGTH_LONG).show()
            }
        }
    }
}
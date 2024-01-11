package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity(){
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

    }
}
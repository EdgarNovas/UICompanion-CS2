package com.example.apirest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var btnRegister : Button
    private lateinit var btnLogin : Button
    private lateinit var btnSteam :  Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        auth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
        btnSteam = findViewById(R.id.btn_login_steam)

        // STEAM
        btnSteam.setOnClickListener {
            val intent = Intent(this, SteamLogin::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener { register() }
        btnLogin.setOnClickListener { login() }
    }


    private fun register(){
        val email = emailField.text.toString()
        val pass = passwordField.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuario creado.", Toast.LENGTH_SHORT).show()
                        irAlMenu()
                    } else {
                        Toast.makeText(this, "Error al crear: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
        }
    }


    private fun login() {
        val email = emailField.text.toString()
        val pass = passwordField.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        irAlMenu()
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irAlMenu() {
        val intent = Intent(this, MainMenu::class.java)
        //Con esto no puede volver hacia atras
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
package com.example.apirest

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        auth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        btnRegister.setOnClickListener { register() }
        btnLogin.setOnClickListener { login() }
    }


    private fun register(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {
                    task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Registro correcto", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SteamLogin::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun login(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {
                    task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    val userID = user?.uid

                    Toast.makeText(this,"Registro correcto", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SteamLogin::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
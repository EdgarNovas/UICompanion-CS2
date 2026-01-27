package com.example.apirest

import CS2API.ChatAdapter
import CS2API.ChatMessage
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var message: EditText
    private lateinit var btnSend: Button

    private lateinit var adapter: ChatAdapter
    private val messagesList = mutableListOf<ChatMessage>()

    private var currentUserName: String = "Cargando..."

    // Realtime Database
    private val dbUrl = "https://cscompanion-ba26b-default-rtdb.europe-west1.firebasedatabase.app/"
    private val chatRef = FirebaseDatabase.getInstance("https://cscompanion-ba26b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("global_chat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TOOLBAR
        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container)
                as? ToolbarFragment

        toolbarFragment?.let {
            it.setToolbarTitle("Chat Global")
            // Boton de atras
            it.setMenuButtonAction {
                finish()
            }
        }

        // Inicializar vistas
        recyclerView = findViewById(R.id.chatRecyclerView)
        message = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Configurar RecyclerView
        adapter = ChatAdapter(messagesList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // Mensajes empiezan desde abajo
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        identificarYCargarUsuario()

        // Escuchar mensajes
        listenForMessages()

        // Enviar mensajes
        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun identificarYCargarUsuario() {
        val authUser = FirebaseAuth.getInstance().currentUser
        val rootRef = FirebaseDatabase.getInstance(dbUrl).reference

        if (authUser != null) {
            //LOGIN POR EMAIL
            val uid = authUser.uid
            val userRef = rootRef.child("usuarios").child(uid)

            userRef.child("nombre").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    currentUserName = snapshot.value.toString()
                } else {
                    // Si no tiene nombre en la BD, usamos su email como respaldo
                    currentUserName = authUser.email?.substringBefore("@") ?: "Usuario Email"
                }
            }.addOnFailureListener {
                currentUserName = "Error Auth"
            }

        } else {
            // LOGIN POR STEAM SharedPreferences
            val prefs = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
            val steamIdGuardado = prefs.getString("steam_id", null)

            if (steamIdGuardado != null) {
                // Combinamos el ID para que coincida con la BD: steam_12345...
                val userIdParaFirebase = "steam_$steamIdGuardado"
                val userRef = rootRef.child("usuarios").child(userIdParaFirebase)

                userRef.child("nombre").get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        currentUserName = snapshot.value.toString()
                    } else {
                        // Respaldo: nombre guardado en preferencias
                        currentUserName = prefs.getString("steam_name", "Gamer Steam") ?: "Gamer"
                    }
                }.addOnFailureListener {
                    currentUserName = "Error Steam"
                }
            } else {

                currentUserName = "Anónimo"
                Toast.makeText(this, "No estás logueado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listenForMessages() {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (childSnapshot in snapshot.children) {
                    val chatMessage = childSnapshot.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        messagesList.add(chatMessage)
                    }
                }
                adapter.notifyDataSetChanged()
                if (messagesList.isNotEmpty()) {
                    recyclerView.scrollToPosition(messagesList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage() {
        val text = message.text.toString()

        if (text.isEmpty()) return

        val user = FirebaseAuth.getInstance().currentUser

        val uid = user?.uid

        val newMessage = ChatMessage(
            message = text,
            sender = currentUserName,
            timestamp = System.currentTimeMillis()
        )

        chatRef.push().setValue(newMessage)
            .addOnSuccessListener {
                message.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
            }
    }
}
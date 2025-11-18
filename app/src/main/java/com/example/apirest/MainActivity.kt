package com.example.apirest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.DataInput
import java.math.BigInteger
import java.security.MessageDigest
import MarvelApi.MarvelApiCall
import MarvelApi.MarvelResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        analytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle().apply {
            putString("portrait_orientation",(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT).toString())
        }


        analytics.logEvent("MyFirstEvent", bundle)


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN){
            val intent = Intent(this, LoadingScreen::class.java)
            startActivity(intent)

        }
        return super.onTouchEvent(event)

    }



}
package com.example.apirest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.res.Configuration
import android.view.MotionEvent
import com.google.firebase.analytics.FirebaseAnalytics


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
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)

        }
        return super.onTouchEvent(event)

    }



}
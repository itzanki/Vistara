package com.example.vistara

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setting click listeners
        findViewById<View>(R.id.btn1).setOnClickListener { v: View? ->
            openActivity(
                HetroMyopia::class.java
            )
        }
        findViewById<View>(R.id.btn2).setOnClickListener { v: View? ->
            openActivity(
                Myopia::class.java
            )
        }
       findViewById<View>(R.id.btn3).setOnClickListener { v: View? ->
            openActivity(com.example.vistara.ColourBlindness::class.java
            )
       }
        findViewById<View>(R.id.btn4).setOnClickListener { v: View? ->
           openActivity(
                ColorRecognition::class.java
            )
        }
//        findViewById<View>(R.id.btn5).setOnClickListener { v: View? ->
//            openActivity(
//                FeedbackActivity::class.java
//            )
//        }
    }

    private fun openActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
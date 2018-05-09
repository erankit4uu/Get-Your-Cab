package com.example.ankit.ubercloneapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife

class MainActivity : AppCompatActivity() {
    lateinit var mbtn_driver: Button
    lateinit var mbtn_rider: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        mbtn_driver = findViewById(R.id.btn_driver)
        mbtn_rider = findViewById(R.id.btn_rider)


        mbtn_driver.setOnClickListener {
            val intent = Intent(this, DriverLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mbtn_rider.setOnClickListener {
            // your code to perform when the user clicks on the button
            val intent = Intent(this, CustomerLoginActivity::class.java)
            startActivity(intent)
            finish()        }
    }
}

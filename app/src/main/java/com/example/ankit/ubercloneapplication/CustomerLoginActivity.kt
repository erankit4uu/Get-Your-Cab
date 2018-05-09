package com.example.ankit.ubercloneapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CustomerLoginActivity : AppCompatActivity(){

    lateinit var met_email : EditText
    lateinit var met_password : EditText
    lateinit var mbtn_register : Button
    lateinit var mbtn_signin : Button

    lateinit var firebaseAuth : FirebaseAuth
    lateinit var firebaseAuthListner : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_login_activity)

        met_email = findViewById(R.id.et_email)
        met_password = findViewById(R.id.et_password)
        mbtn_signin = findViewById(R.id.btn_signin)
        mbtn_register = findViewById(R.id.btn_register)

        firebaseAuth = FirebaseAuth.getInstance()


        firebaseAuthListner = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@CustomerLoginActivity, CustomerMapActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }


        mbtn_register.setOnClickListener(View.OnClickListener {
            val email = met_email.text.toString()
            val password = met_password.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@CustomerLoginActivity, OnCompleteListener<AuthResult> { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this@CustomerLoginActivity, "sign up error", Toast.LENGTH_SHORT).show()
                        } else {
                            val userId = firebaseAuth.currentUser?.uid
                            val currentUserdb = FirebaseDatabase.getInstance().reference.child("Users").child("Customers").child(userId)
                            currentUserdb.setValue(email)
                        }
                    })
        })

        mbtn_signin.setOnClickListener{
            val email = met_email.text.toString()
            val password = met_password.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    OnCompleteListener <AuthResult>{ task ->
                        if (!task.isSuccessful){
                            Toast.makeText(this@CustomerLoginActivity, "sign In error", Toast.LENGTH_SHORT).show()
                        }
                    })


        }

    }

    override fun onStart() {
        super.onStart()

        firebaseAuth.addAuthStateListener(firebaseAuthListner)

    }

    override fun onStop() {
        super.onStop()

        firebaseAuth.removeAuthStateListener(firebaseAuthListner)
    }
}
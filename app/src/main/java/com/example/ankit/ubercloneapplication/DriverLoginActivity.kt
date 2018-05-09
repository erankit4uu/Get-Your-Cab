package com.example.ankit.ubercloneapplication

import android.app.Activity
import android.content.Intent
import android.inputmethodservice.ExtractEditText
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.driver_login_activity.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull



class DriverLoginActivity : Activity() {

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
                val intent = Intent(this@DriverLoginActivity, DriverMapActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }


        mbtn_register.setOnClickListener(View.OnClickListener {
            val email = met_email.text.toString()
            val password = met_password.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@DriverLoginActivity, OnCompleteListener<AuthResult> { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@DriverLoginActivity, "sign up error", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = firebaseAuth.currentUser?.uid
                    val currentUserdb = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(userId)
                    currentUserdb.setValue(email)
                }
            })
        })

        mbtn_signin.setOnClickListener{
            val email = met_email.text.toString()
            val password = met_password.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    OnCompleteListener <AuthResult>{task ->
                        if (!task.isSuccessful){
                            Toast.makeText(this@DriverLoginActivity, "sign In error", Toast.LENGTH_SHORT).show()
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
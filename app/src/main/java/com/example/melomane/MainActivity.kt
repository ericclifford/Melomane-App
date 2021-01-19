package com.example.melomane

import android.content.Intent
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


const val RC_SIGN_IN = 123

class MainActivity : AppCompatActivity(){
    private val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnLogin.setOnClickListener(View.OnClickListener {
            view -> login()
        })

        val txtRegister = findViewById<View>(R.id.txt_register) as TextView
        txtRegister.setOnClickListener(View.OnClickListener{
            view -> register()
        })
    }

    private fun register() {
        startActivity(Intent(this, Register :: class.java))
    }

    private fun login() {
        val txtEmail = findViewById<View>(R.id.txt_email) as EditText
        val txtPassword = findViewById<View>(R.id.txt_password) as EditText

        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
                task ->
                if (task.isSuccessful){
                    startActivity(Intent(this, Timeline :: class.java))
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                }
            })
        }
        else {
            Toast.makeText(this, "Please enter login info.", Toast.LENGTH_LONG).show()
        }
    }

}

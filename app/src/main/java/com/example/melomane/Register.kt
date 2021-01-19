package com.example.melomane

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabase :DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        val btn_register = findViewById<View>(R.id.btn_register) as Button
        btn_register.setOnClickListener(View.OnClickListener {
            view -> register()
        })
    }

    private fun register() {
        val txtEmail = findViewById<View>(R.id.txt_email) as EditText
        val txtName = findViewById<View>(R.id.txt_name) as EditText
        val txtPassword = findViewById<View>(R.id.txt_password) as EditText
        val txtPasswordConf = findViewById<View>(R.id.txt_passwordConf) as EditText
        val email = txtEmail.text.toString()
        var name = txtName.text.toString()
        val password = txtPassword.text.toString()
        val passwordConf = txtPasswordConf.text.toString()

        if(email.isNotEmpty() && name.isNotEmpty() &&
            password.isNotEmpty() && passwordConf.isNotEmpty()){
            if(password == passwordConf){
                if(password.length >= 8){
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
                            task -> if(task.isSuccessful){
                                val user = mAuth.currentUser
                                val uid = user!!.uid
                                mDatabase.child(uid).child("Name").setValue(name)
                                Toast.makeText(this, "Registration Succeeded.", Toast.LENGTH_LONG).show()
                                //startActivity(Intent(this, MainActivity :: class.java))
                            }
                            else{
                            Toast.makeText(this, "Registration Failed.", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                    else{
                        Toast.makeText(this, "Please enter a valid email " +
                                "(ex: user@domain.com)", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "Password must be at least 8 characters.",
                        Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Passwords must match.", Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this, "Must enter all fields.", Toast.LENGTH_LONG).show()
        }

    }
}
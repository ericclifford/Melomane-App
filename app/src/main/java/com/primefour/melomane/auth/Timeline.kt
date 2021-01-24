package com.primefour.melomane.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.primefour.melomane.R

class Timeline : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        val txtName = findViewById<View>(R.id.lbl_name) as TextView
        mDatabase = FirebaseDatabase.getInstance().getReference("Names")
        mDatabase.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("Name").toString()
                txtName.text = "Hello "
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
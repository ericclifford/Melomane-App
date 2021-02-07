package main.app.melomane

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import main.app.melomane.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth


const val RC_SIGN_IN = 1

class LoginEmail : AppCompatActivity(){
    private val mAuth = FirebaseAuth.getInstance()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginemail)
        val btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnLogin.setOnClickListener(View.OnClickListener {
            view -> login()
        })

        val txtRegister = findViewById<View>(R.id.txt_register) as TextView
        txtRegister.setOnClickListener(View.OnClickListener{
            view -> register()
        })
        val googleSignIn = findViewById<View>(R.id.sign_in_button) as SignInButton
        googleSignIn.setOnClickListener {
            view: View? -> googleLogin()
        }
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleLogin() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            updateUI(account)
        }
        catch(e: ApiException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        startActivity(Intent(this, Timeline :: class.java))
        val name = account.displayName
        Toast.makeText(this, "Welcome, $name", Toast.LENGTH_LONG).show()
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

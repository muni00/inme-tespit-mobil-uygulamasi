package com.muazzeznihalbahadir.inmetespituygulamasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.muazzeznihalbahadir.inmetespituygulamasi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        val view = binding.root

        setContentView(view)

        binding.kayitButton.setOnClickListener {
            kayitOl()
        }
    }
    fun kayitOl() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "başarılı genco", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener { exception ->
            //Log.e("signup", "${exception.printStackTrace()}")
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }
}
package com.muazzeznihalbahadir.inmetespituygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muazzeznihalbahadir.inmetespituygulamasi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val view = binding.root

        setContentView(view)

        val guncelKullanici = auth.currentUser
        if(guncelKullanici != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.kayitButton.setOnClickListener {
            kayitOl()
        }

        binding.girisButton.setOnClickListener {
            girisYap()

        }
    }
    private fun kayitOl() {
        val userName = binding.userName.text.toString()
        val userPower = "yetkisiz"
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "başarılı genco", Toast.LENGTH_LONG).show()
                val userHashMap = hashMapOf<String,Any>()
                userHashMap.put("userName",userName)
                userHashMap.put("userEmail",email)
                userHashMap.put("userPassword",password)
                userHashMap.put("userPower",userPower)

                firestore.collection("user").add(userHashMap).addOnCompleteListener { itask ->
                    if(itask.isSuccessful){
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)

                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }

        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }
    private fun girisYap(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){

                val guncelKullanici = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin : ${guncelKullanici}",Toast.LENGTH_LONG).show()

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }

}
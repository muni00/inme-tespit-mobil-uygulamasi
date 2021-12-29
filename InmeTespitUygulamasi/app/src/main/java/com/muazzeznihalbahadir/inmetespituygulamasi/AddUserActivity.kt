package com.muazzeznihalbahadir.inmetespituygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muazzeznihalbahadir.inmetespituygulamasi.databinding.ActivityAddUserBinding
import com.muazzeznihalbahadir.inmetespituygulamasi.model.User


class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private  lateinit var firestore : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var userListesi = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.klKaydetBtn.setOnClickListener {
            kullaniciEkle()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var selectedObsion = ""
        when(item.itemId){
            R.id.anasayfaya_don->selectedObsion="Anasayfaya Dön"
            R.id.kullanici_ekle->selectedObsion="Kullanici Ekle"
            R.id.cikis_yap->selectedObsion="Çıkış Yap"
        }
        if (selectedObsion == "Anasayfaya Dön")
        {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if (selectedObsion == "Kullanici Ekle")
        {
            val intent = Intent(this,AddUserActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (selectedObsion == "Çıkış Yap")
        {
            auth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    private  fun kullaniciEkle(){

        val userName = binding.kullaniciAdi.text.toString()
        val userEmail = binding.klEmail.text.toString()
        val userPassword = binding.klPassword.text.toString()
        val userPower = binding.klYetki.text.toString()

        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "başarılı genco", Toast.LENGTH_LONG).show()

                val userHashMap = hashMapOf<String,Any>()
                userHashMap.put("userName",userName)
                userHashMap.put("userEmail",userEmail)
                userHashMap.put("userPassword",userPassword)
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
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // menu?.getItem(1)?.isVisible = false
        val crEmail =  auth.currentUser?.email.toString()
        try {
            firestore.collection("user")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                val documents = snapshot.documents
                                userListesi.clear()
                                for (document in documents) {
                                    val userEmail = document.get("userEmail")
                                    val userPower = document.get("userPower")
                                    if (crEmail == userEmail.toString()) {
                                        if (userPower.toString() == "yetkili") {
                                            menu?.getItem(1)?.setEnabled(false)
                                        } else if (userPower.toString() == "yetkisiz") {
                                            menu?.getItem(1)?.setEnabled(false)
                                        }else {
                                            Toast.makeText(
                                                this,
                                                "admin kullanici ekleyebilir",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "...",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                }
                                // adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return super.onPrepareOptionsMenu(menu)
    }

}
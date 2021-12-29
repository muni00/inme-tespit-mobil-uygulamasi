package com.muazzeznihalbahadir.inmetespituygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muazzeznihalbahadir.inmetespituygulamasi.databinding.ActivityMainBinding
import com.muazzeznihalbahadir.inmetespituygulamasi.model.Questions
import com.muazzeznihalbahadir.inmetespituygulamasi.model.User
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    private var sorulistesi = ArrayList<Questions>()
    private var userListesi = ArrayList<User>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



    }

    override fun onResume() {
        super.onResume()
        veriAl()
        binding.buttonTest.setOnClickListener {
            var sayac : Int = 0
            val alertDialog = AlertDialog.Builder(this)

            for ((index, soru) in sorulistesi.withIndex()) {

                alertDialog.setTitle("Soru : ${Math.abs(index - sorulistesi.size)}")
                alertDialog.setMessage(soru.text)
                alertDialog.setPositiveButton("Evet") { dialog, witch ->
                    sayac += soru.deger
                    if (sayac in 0..40){
                        binding.textView.setText("ihtimal yüzdesi : ${sayac.toString()} \nRiski az")

                    }else if (sayac in 41..60){
                        binding.textView.setText("ihtimal yüzdesi : ${sayac.toString()} \nRiskli")

                    }else if (sayac in 61..70){
                        binding.textView.setText("ihtimal yüzdesi : ${sayac.toString()} \nUzmana Başvurmalı")

                    }else{
                        binding.textView.setText("ihtimal yüzdesi : ${sayac.toString()} \nAcil Müdahale")
                    }
                }
                alertDialog.setNegativeButton("Hayır") { dialog, witch ->
                }
                alertDialog.show()
            }




        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var selectedObsion = ""
        when (item.itemId) {
            R.id.anasayfaya_don -> selectedObsion = "Anasayfaya Dön"
            R.id.kullanici_ekle -> selectedObsion = "Kullanici Ekle"
            R.id.soru_ekle -> selectedObsion = "Soru Ekle"
            R.id.cikis_yap -> selectedObsion = "Çıkış Yap"
        }
        if (selectedObsion == "Anasayfaya Dön") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (selectedObsion == "Kullanici Ekle") {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
            finish()
        }else if (selectedObsion == "Soru Ekle") {
            val intent = Intent(this, AddQuestionActivity::class.java)
            startActivity(intent)
            finish()
        }else if (selectedObsion == "Çıkış Yap") {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // menu?.getItem(1)?.isVisible = false
        val crEmail = auth.currentUser?.email.toString()
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
                                            menu?.getItem(2)?.setEnabled(false)
                                            break

                                        } else if (userPower.toString() == "yetkisiz") {
                                            menu?.getItem(1)?.setEnabled(false)
                                            menu?.getItem(2)?.setEnabled(false)

                                            break
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "admin kullanici ekleyebilir",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                }
                                // adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun veriAl() {
        try {
            firestore.collection("questions")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                val documents = snapshot.documents
                                sorulistesi.clear()
                                for (document in documents) {
                                    val deger = document.get("deger")
                                    val id = document.get("id")
                                    val text = document.get("text")

                                    val questions = Questions(deger.toString().toInt(),id.toString(), text.toString())
                                    sorulistesi.add(questions)



                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
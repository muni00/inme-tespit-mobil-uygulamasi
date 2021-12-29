package com.muazzeznihalbahadir.inmetespituygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.muazzeznihalbahadir.inmetespituygulamasi.databinding.ActivityAddQuestionBinding
import com.muazzeznihalbahadir.inmetespituygulamasi.model.Questions

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAddQuestionBinding
    private lateinit var firestore: FirebaseFirestore
    private var sorulistesi = ArrayList<Questions>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        firestore = FirebaseFirestore.getInstance()

        veriAl()
        binding.qtKaydetBtn.setOnClickListener {
           // veriAl()
            soruEkle(sorulistesi.size)
        }

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
    private fun soruEkle(boyut:Int){

            var size = boyut+1
            val soruText = binding.soruText.text.toString()
            val degerView = binding.degerText.text.toString()
            val id = size.toString()

            val userHashMap = hashMapOf<String,Any>()
            userHashMap.put("deger",degerView)
            userHashMap.put("id",id)
            userHashMap.put("text",soruText)

            firestore.collection("questions").add(userHashMap).addOnCompleteListener { itask ->
                if(itask.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }

}



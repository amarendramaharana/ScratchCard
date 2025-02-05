package com.aptech.scratchview

import android.graphics.*
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      /*  val btnSet = findViewById<MaterialButton>(R.id.btnSet)
        val edOff = findViewById<EditText>(R.id.edDiscount)
        val txtOff = findViewById<TextView>(R.id.txtOff)
        btnSet.setOnClickListener {
            txtOff.text = "${edOff.text}% OFF"
            edOff.text = null
        }*/
    }


}
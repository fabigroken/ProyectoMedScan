package com.fabigroken.proyectomedscan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** activity_main ahora solo contiene el NavHostFragment */
        setContentView(R.layout.activity_main)
    }
}

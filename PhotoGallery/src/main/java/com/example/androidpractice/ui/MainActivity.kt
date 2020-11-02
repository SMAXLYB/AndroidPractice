package com.example.androidpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.androidpractice.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NavigationUI.setupActionBarWithNavController(this, findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragment).navigateUp()
    }
}


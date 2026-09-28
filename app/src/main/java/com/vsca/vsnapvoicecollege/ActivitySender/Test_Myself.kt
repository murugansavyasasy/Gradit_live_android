package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat

import com.vsca.vsnapvoicecollege.R


class Test_Myself : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_myself)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false



    }
}
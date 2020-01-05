package com.anwesh.uiprojects.linkedcorsqfilllinebouncyview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.corsqfilllinebouncyview.CorSqFillLineBouncyView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CorSqFillLineBouncyView.create(this)
    }
}

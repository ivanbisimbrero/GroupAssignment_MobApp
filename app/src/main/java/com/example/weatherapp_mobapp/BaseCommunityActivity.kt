package com.example.weatherapp_mobapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp_mobapp.adapter.CommentAdapter
import com.example.weatherapp_mobapp.databinding.ActivityCityPostedCommentsBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date

abstract class BaseCommunityActivity : AppCompatActivity() {
    private val database = Firebase.database("https://grouptask-mobapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var dbReference: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private var isEditing = false
    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    private var currentInitDate = sdf.format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_community)
    }
}
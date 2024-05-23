package com.example.weatherapp_mobapp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp_mobapp.adapter.CommentAdapter
import com.example.weatherapp_mobapp.adapter.MessageAdapter
import com.example.weatherapp_mobapp.databinding.ActivityA5CityDetailBinding
import com.example.weatherapp_mobapp.databinding.ActivityCityPostedCommentsBinding
import com.example.weatherapp_mobapp.model.Comment
import com.example.weatherapp_mobapp.model.Message
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_KEY_COMMENTS
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_mobapp.sharedPreferences.SharedPreferencesRepository
import com.example.weatherapp_mobapp.utils.DataUtils
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date

class CityPostedComments : AppCompatActivity() {
    private val view by lazy { ActivityCityPostedCommentsBinding.inflate(layoutInflater) }
    private val database = Firebase.database("https://grouptask-mobapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var dbReference: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private var isEditing = false
    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            ), SHARED_PREFERENCES_KEY_COMMENTS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        //Setup database reference
        val cityName = intent.getStringExtra("cityName")!!
        dbReference = database.getReference(cityName)

        //Setup variable text from the layout
        view.comments.etChatUsername.setText(DataUtils.mainUser.name)
        view.comments.etChatEmail.setText(DataUtils.mainUser.email)

        //Setup the message adapter
        commentAdapter = CommentAdapter(mutableListOf(),repository)
        view.comments.rvChatMessages.adapter = commentAdapter
        view.comments.rvChatMessages.layoutManager = LinearLayoutManager(this)
        commentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                view.comments.rvChatMessages.layoutManager!!.scrollToPosition(commentAdapter.itemCount - 1)
            }
        })

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val r = Rect()
                rootView.getWindowVisibleDisplayFrame(r)
                val screenHeight = rootView.rootView.height

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than the screen height.
                val keypadHeight = screenHeight - r.bottom

                // 0.15 ratio is perhaps enough to determine keypad height.
                if (keypadHeight > screenHeight * 0.15) {
                    // keyboard is opened
                    view.comments.rvChatMessages.layoutManager!!.scrollToPosition(commentAdapter.itemCount - 1)
                }
            }
        })

        //Setup the buttons
        view.sendComment.btnPostComment.setOnClickListener {
            if(view.sendComment.etComments.text.toString().isNotEmpty() && !haveEmptyUsernameAndEmail()) run {
                val currentDate = sdf.format(Date())
                println(currentDate)
                val newMessageKey = dbReference.push().key!!
                val comment = Comment(
                    newMessageKey, DataUtils.mainUser.name, DataUtils.mainUser.email,
                    view.sendComment.etComments.text.toString(), currentDate, false
                )
                //We first push it to the db, with the message key that we have obtained from the db
                dbReference.child(newMessageKey).setValue(comment)
                //Now as the message for us is going to be outgoing, we set the boolean to true
                comment.isCurrentUser = true
                //And finally, we add it to our adapter
                commentAdapter.insertNewComment(comment)
                repository.save(cityName+"_"+comment) //!!!!!!!!!!!!!!!GUARDO MAL CREO!!!!!!!!!!!!!!
                view.sendComment.etComments.setText("")
            } else {
                Toast.makeText(this, "Please, set up an email and username", Toast.LENGTH_SHORT).show()
            }
        }
        view.comments.btnChange.setOnClickListener {
            if (isEditing) {
                // Save values and disable edit text
                DataUtils.mainUser.name = view.comments.etChatUsername.text.toString()
                DataUtils.mainUser.email = view.comments.etChatEmail.text.toString()
                view.comments.etChatUsername.isEnabled = false
                view.comments.etChatEmail.isEnabled = false
                view.comments.btnChange.text = "Change"
            } else {
                // Enable edit text
                view.comments.etChatUsername.isEnabled = true
                view.comments.etChatEmail.isEnabled = true
                view.comments.etChatUsername.setText(DataUtils.mainUser.name)
                view.comments.etChatEmail.setText(DataUtils.mainUser.email)
                view.comments.btnChange.text = "Set"
            }
            isEditing = !isEditing
        }

        dbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                //Check if the email is the same as the user one and that the comment isn't yet in the adapter
                if(comment!!.email == DataUtils.mainUser.email &&
                    commentAdapter.commentsList.none { it.id == comment.id }) {
                    comment.isCurrentUser = true
                    commentAdapter.insertNewComment(comment)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun haveEmptyUsernameAndEmail(): Boolean {
        return DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()
    }
}
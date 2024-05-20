package com.example.weatherapp_mobapp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp_mobapp.adapter.MessageAdapter
import com.example.weatherapp_mobapp.databinding.ActivityCityChatBinding
import com.example.weatherapp_mobapp.model.Message
import com.example.weatherapp_mobapp.utils.DataUtils
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date

class CityChatActivity : AppCompatActivity() {
    private val view by lazy { ActivityCityChatBinding.inflate(layoutInflater) }
    private val database = Firebase.database("https://grouptask-mobapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var dbReference: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private var isEditing = false
    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    private var currentInitDate = sdf.format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        //Setup database reference
        val cityName = intent.getStringExtra("cityName")!!
        dbReference = database.getReference(cityName)

        //Setup variable text from the layout
        view.etChatUsername.setText(DataUtils.mainUser.name)
        view.etChatEmail.setText(DataUtils.mainUser.email)

        //Setup the message adapter
        messageAdapter = MessageAdapter(mutableListOf())
        view.rvChatMessages.adapter = messageAdapter
        view.rvChatMessages.layoutManager = LinearLayoutManager(this)
        messageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                view.rvChatMessages.layoutManager!!.scrollToPosition(messageAdapter.itemCount - 1)
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
                    view.rvChatMessages.layoutManager!!.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        })

        //Setup the buttons
        view.btnSend.setOnClickListener {
            if(view.etMessages.text.toString().isNotEmpty() && !haveEmptyUsernameAndEmail()) run {
                val currentDate = sdf.format(Date())
                println(currentDate)
                val newMessageKey = dbReference.push().key!!
                val message = Message(
                    newMessageKey ,DataUtils.mainUser.name, DataUtils.mainUser.email,
                    view.etMessages.text.toString(), currentDate, false
                )
                //We first push it to the db, with the message key that we have obtained from the db
                dbReference.child(newMessageKey).setValue(message)
                //Now as the message for us is going to be outgoing, we set the boolean to true
                message.isCurrentUser = true
                //And finally, we add it to our adapter
                messageAdapter.insertNewMessage(message)
                view.etMessages.setText("")
            } else {
                Toast.makeText(this, "Please, set up an email and username", Toast.LENGTH_SHORT).show()
            }
        }
        view.btnChange.setOnClickListener {
            if (isEditing) {
                // Save values and disable edit text
                DataUtils.mainUser.name = view.etChatUsername.text.toString()
                DataUtils.mainUser.email = view.etChatEmail.text.toString()
                view.etChatUsername.isEnabled = false
                view.etChatEmail.isEnabled = false
                view.btnChange.text = "Change"
            } else {
                // Enable edit text
                view.etChatUsername.isEnabled = true
                view.etChatEmail.isEnabled = true
                view.etChatUsername.setText(DataUtils.mainUser.name)
                view.etChatEmail.setText(DataUtils.mainUser.email)
                view.btnChange.text = "Set"
            }
            isEditing = !isEditing
        }

        dbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null && message.id.isNotEmpty() && messageAdapter.messageList.none { it.id == message.id }) {
                    //If the hour of the message is greather than the initHour, we add the message
                    if(message.hour > currentInitDate) {
                        messageAdapter.insertNewMessage(message)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun onResume() {
        super.onResume()
        currentInitDate = sdf.format(Date())
    }

    private fun haveEmptyUsernameAndEmail(): Boolean {
        return DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()
    }
}
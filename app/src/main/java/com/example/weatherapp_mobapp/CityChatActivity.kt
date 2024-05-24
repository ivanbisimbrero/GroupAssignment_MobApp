package com.example.weatherapp_mobapp

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
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
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_KEY_USER
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_mobapp.sharedPreferences.SharedPreferencesRepository
import com.example.weatherapp_mobapp.utils.DataUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class CityChatActivity : AppCompatActivity() {
    private val view by lazy { ActivityCityChatBinding.inflate(layoutInflater) }
    private val database = FirebaseDatabase.getInstance("https://grouptask-mobapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var dbReference: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private var isEditing = false
    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    private var currentInitDate = sdf.format(Date())
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            ), SHARED_PREFERENCES_KEY_USER
        )
    }
    private val REQUEST_CODE_IMAGE_PICK = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        //Setup database reference
        val cityName = intent.getStringExtra("cityName")!!
        view.chat.tvModelCityName.text = cityName.plus(" Chat")
        dbReference = database.getReference(cityName)

        //Setup variable text from the layout
        view.chat.etChatUsername.setText(DataUtils.mainUser.name)
        view.chat.etChatEmail.setText(DataUtils.mainUser.email)

        //Setup the message adapter
        messageAdapter = MessageAdapter(mutableListOf())
        view.chat.rvChatMessages.adapter = messageAdapter
        view.chat.rvChatMessages.layoutManager = LinearLayoutManager(this)
        messageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                view.chat.rvChatMessages.layoutManager!!.scrollToPosition(messageAdapter.itemCount - 1)
            }
        })

        // Change the button text
        if (DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()){
            view.chat.btnChange.text = "Login"
        } else {
            view.chat.btnChange.text = "Change Username"
            // Set the visibility of the EditText fields to GONE
            view.chat.etChatUsername.visibility = View.GONE
            view.chat.etChatEmail.visibility = View.GONE
        }

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
                    view.chat.rvChatMessages.layoutManager!!.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        })

        //Setup the buttons
        view.sendMessage.btnSend.setOnClickListener {
            if(haveEmptyUsernameOrEmail()) {
                Toast.makeText(this, "Please, set up an email and username", Toast.LENGTH_SHORT).show()
            } else if(view.sendMessage.etMessages.text.toString().isNotEmpty()) {
                val currentDate = sdf.format(Date())
                val newMessageKey = dbReference.push().key!!
                val message = Message(
                    newMessageKey, DataUtils.mainUser.name, DataUtils.mainUser.email,
                    view.sendMessage.etMessages.text.toString(), currentDate, false
                )
                //We first push it to the db, with the message key that we have obtained from the db
                dbReference.child(newMessageKey).setValue(message)
                //Now as the message for us is going to be outgoing, we set the boolean to true
                message.isCurrentUser = true
                //And finally, we add it to our adapter
                messageAdapter.insertNewMessage(message)
                view.sendMessage.etMessages.setText("")
            }
        }
        view.sendMessage.btnAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }
        view.chat.btnChange.setOnClickListener {
            //Check if the fields contains ; that is a illegal character
            if(view.chat.etChatEmail.text.toString().contains(";") ||
                view.chat.etChatUsername.text.toString().contains(";")) {
                Toast.makeText(this, "Illegal character -> ;. Remove it!", Toast.LENGTH_SHORT).show()
                isEditing = false
            }
            if (isEditing) {
                // Save values and disable edit text
                DataUtils.mainUser.name = view.chat.etChatUsername.text.toString()
                DataUtils.mainUser.email = view.chat.etChatEmail.text.toString()
                view.chat.etChatUsername.isEnabled = false
                view.chat.etChatEmail.isEnabled = false

                // Set the visibility of the EditText fields to GONE
                view.chat.etChatUsername.visibility = View.GONE
                view.chat.etChatEmail.visibility = View.GONE

                //Saved user in shared preferences
                repository.save(DataUtils.mainUser.name + ";" + DataUtils.mainUser.email)

                // Change the button text
                if (DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()){
                    view.chat.btnChange.text = "Login"
                } else {
                    view.chat.btnChange.text = "Change Username"
                }
            } else {
                // Enable edit text
                view.chat.etChatUsername.isEnabled = true
                view.chat.etChatEmail.isEnabled = true
                view.chat.etChatUsername.setText(DataUtils.mainUser.name)
                view.chat.etChatEmail.setText(DataUtils.mainUser.email)
                view.chat.btnChange.text = "Set"

                // Set the visibility of the EditText fields to VISIBLE
                view.chat.etChatUsername.visibility = View.VISIBLE
                view.chat.etChatEmail.visibility = View.VISIBLE

                //Delete current user from sharedPreferences
                repository.delete(DataUtils.mainUser.name + ";" + DataUtils.mainUser.email)
            }
            isEditing = !isEditing
        }

        dbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null && message.id.isNotEmpty() &&
                    messageAdapter.messageList.none { it.id == message.id }) {
                    //If the hour of the message is greater than the initHour, we add the message
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            println(imageUri)
            if (imageUri != null) {
                uploadImageToFirebase(imageUri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageReference.putFile(imageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                saveImageMessage(uri.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get image url", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageMessage(imageUrl: String) {
        val currentDate = sdf.format(Date())
        val newMessageKey = dbReference.push().key!!
        val message = Message(
            newMessageKey, DataUtils.mainUser.name, DataUtils.mainUser.email,
            imageUrl, currentDate, false, true
        )
        dbReference.child(newMessageKey).setValue(message)
        message.isCurrentUser = true
        messageAdapter.insertNewMessage(message)
    }

    private fun haveEmptyUsernameOrEmail(): Boolean {
        return DataUtils.mainUser.name.isEmpty() || DataUtils.mainUser.email.isEmpty()
    }
}

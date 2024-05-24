package com.example.weatherapp_mobapp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp_mobapp.adapter.CommentAdapter
import com.example.weatherapp_mobapp.databinding.ActivityCityPostedCommentsBinding
import com.example.weatherapp_mobapp.model.Comment
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_KEY_USER
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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


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
            ), SHARED_PREFERENCES_KEY_USER
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        //Setup database reference
        val cityName = intent.getStringExtra("cityName")!!
        view.comments.tvModelCityName.text = cityName
        dbReference = database.getReference("Comments_${cityName}")

        //Setup variable text from the layout
        view.comments.etChatUsername.setText(DataUtils.mainUser.name)
        view.comments.etChatEmail.setText(DataUtils.mainUser.email)

        //Setup the comment adapter
        commentAdapter = CommentAdapter(mutableListOf()) { comment ->
            deleteComment(comment)
        }
        view.comments.rvChatMessages.adapter = commentAdapter
        view.comments.rvChatMessages.layoutManager = LinearLayoutManager(this)
        commentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                //When a new item is inserted in the adapter, it autoscrolls down, like in a real chat
                super.onItemRangeInserted(positionStart, itemCount)
                view.comments.rvChatMessages.layoutManager!!.scrollToPosition(commentAdapter.itemCount - 1)
            }
        })

        // Change the button text
        if (DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()){
            view.comments.btnChange.text = "Login"
        } else {
            view.comments.btnChange.text = "Change Username"
            // Set the visibility of the EditText fields to GONE
            view.comments.etChatUsername.visibility = View.GONE
            view.comments.etChatEmail.visibility = View.GONE
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
                    view.comments.rvChatMessages.layoutManager!!.scrollToPosition(commentAdapter.itemCount - 1)
                }
            }
        })

        //Setup the buttons
        view.sendComment.btnPostComment.setOnClickListener {
            if(haveEmptyUsernameOrEmail()) {
                Toast.makeText(this, "Please, set up an email and username", Toast.LENGTH_SHORT)
                    .show()
            } else if(view.sendComment.etComments.text.toString().isNotEmpty()) run {
                //We format the date in order to be the same for all users
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
                view.sendComment.etComments.setText("")
            }
        }
        view.comments.btnChange.setOnClickListener {
            //As we use the ";" in the parser in shared preferences, we deny the use of the character.
            if(view.comments.etChatEmail.text.toString().contains(";") ||
                view.comments.etChatUsername.text.toString().contains(";")) {
                Toast.makeText(this, "Ilegal character -> ;. Remove it!", Toast.LENGTH_SHORT).show()
                isEditing = false
            }
            if (isEditing) {
                // Save values and disable edit text
                DataUtils.mainUser.name = view.comments.etChatUsername.text.toString()
                DataUtils.mainUser.email = view.comments.etChatEmail.text.toString()
                view.comments.etChatUsername.isEnabled = false
                view.comments.etChatEmail.isEnabled = false

                //Saved user in shared preferences
                repository.save(DataUtils.mainUser.name + ";" + DataUtils.mainUser.email)

                // Set the visibility of the EditText fields to GONE
                view.comments.etChatUsername.visibility = View.GONE
                view.comments.etChatEmail.visibility = View.GONE

                // Change the button text
                if (DataUtils.mainUser.name.isEmpty() && DataUtils.mainUser.email.isEmpty()){
                    view.comments.btnChange.text = "Login"
                    //If the user needs to login, we show him the fields
                    view.comments.etChatUsername.visibility = View.VISIBLE
                    view.comments.etChatEmail.visibility = View.VISIBLE
                } else {
                    //If he is currently logged in, we show him "Change Username"
                    view.comments.btnChange.text = "Change Username"
                }

                //Change the boolean inside the adapter
                commentAdapter.commentsList.forEach {
                    if(it.email == DataUtils.mainUser.email) {
                        it.isCurrentUser = true
                    } else {
                        it.isCurrentUser = false
                    }
                }

                //Check if is current user
                commentAdapter.notifyDataSetChanged()
            } else {
                // Enable edit text
                view.comments.etChatUsername.isEnabled = true
                view.comments.etChatEmail.isEnabled = true
                view.comments.etChatUsername.setText(DataUtils.mainUser.name)
                view.comments.etChatEmail.setText(DataUtils.mainUser.email)
                view.comments.btnChange.text = "Set"

                // Set the visibility of the EditText fields to VISIBLE
                view.comments.etChatUsername.visibility = View.VISIBLE
                view.comments.etChatEmail.visibility = View.VISIBLE

                //Delete current user from sharedPreferences
                repository.delete(DataUtils.mainUser.name + ";" + DataUtils.mainUser.email)

            }
            isEditing = !isEditing
        }

        dbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                //Check that the comment isn't yet in the adapter
                if(commentAdapter.commentsList.none { it.id == comment!!.id }){
                    //If the user has the same email as the comment, we suppose he is the author and that he can remove it
                    if(comment!!.email == DataUtils.mainUser.email) {
                        comment.isCurrentUser = true
                        commentAdapter.insertNewComment(comment)
                    } else {
                        comment.isCurrentUser = false
                        commentAdapter.insertNewComment(comment)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {
                //We retrieve the removed comment, and we use the adapter method removeComment to remove it
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    commentAdapter.removeComment(comment)
                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        //Setup swipe to delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //We get the item position in order to delete in when the user has swiped the comment
                val position = viewHolder.adapterPosition
                val comment = commentAdapter.commentsList[position]
                if (comment.isCurrentUser){
                    deleteComment(comment)
                }else{
                    commentAdapter.notifyItemChanged(position)
                    Toast.makeText(this@CityPostedComments, "You can only delete your own comments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //We paint with red the swipe
                val itemView = viewHolder.itemView
                val background = Paint().apply { color = Color.RED }

                if  (dX < 0) { // Swiping to the left
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        background
                    )
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        //Finally, we add the action swipe to delete to the recycler view
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(view.comments.rvChatMessages)

    }

    private fun haveEmptyUsernameOrEmail(): Boolean {
        return DataUtils.mainUser.name.isEmpty() || DataUtils.mainUser.email.isEmpty()
    }

    private fun deleteComment(comment: Comment) {
        //We get the item with his id, ann we try to remove it. If success, we remove it from the adapter
        dbReference.child(comment.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                commentAdapter.removeComment(comment)
            } else {
                Toast.makeText(this, "Error deleting comment", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
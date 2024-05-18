package com.example.weatherapp_mobapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp_mobapp.R
import com.example.weatherapp_mobapp.model.Message

class MessageAdapter(val messageList: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_OUTGOING = 1
    private val VIEW_TYPE_INCOMING = 2

    class OutgoingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageUsername: TextView = itemView.findViewById(R.id.tvMessageUsername)
        val tvMessageHour: TextView = itemView.findViewById(R.id.tvMessageHour)
        val tvMessageText: TextView = itemView.findViewById(R.id.tvMessageText)
    }

    class IncomingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageUsername: TextView = itemView.findViewById(R.id.tvMessageUsername)
        val tvMessageHour: TextView = itemView.findViewById(R.id.tvMessageHour)
        val tvMessageText: TextView = itemView.findViewById(R.id.tvMessageText)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isCurrentUser) {
            VIEW_TYPE_OUTGOING
        } else {
            VIEW_TYPE_INCOMING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_OUTGOING) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_outgoing, parent, false)
            OutgoingMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_incoming, parent, false)
            IncomingMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder is OutgoingMessageViewHolder) {
            holder.tvMessageUsername.text = currentMessage.username
            holder.tvMessageHour.text = currentMessage.hour
            holder.tvMessageText.text = currentMessage.message
        } else if (holder is IncomingMessageViewHolder) {
            holder.tvMessageUsername.text = currentMessage.username
            holder.tvMessageHour.text = currentMessage.hour
            holder.tvMessageText.text = currentMessage.message
        }
    }

    override fun getItemCount() = messageList.size

    fun insertNewMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }
}
package com.example.weatherapp_mobapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp_mobapp.R
import com.example.weatherapp_mobapp.model.Message

class MessageAdapter(val messageList: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_OUTGOING = 1
    private val VIEW_TYPE_INCOMING = 2
    private val VIEW_TYPE_OUTGOING_IMAGE = 3
    private val VIEW_TYPE_INCOMING_IMAGE = 4

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

    class OutgoingImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageUsername: TextView = itemView.findViewById(R.id.tvMessageUsername)
        val tvMessageHour: TextView = itemView.findViewById(R.id.tvMessageHour)
        val ivMessageImage: ImageView = itemView.findViewById(R.id.ivMessageImage)
    }

    class IncomingImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageUsername: TextView = itemView.findViewById(R.id.tvMessageUsername)
        val tvMessageHour: TextView = itemView.findViewById(R.id.tvMessageHour)
        val ivMessageImage: ImageView = itemView.findViewById(R.id.ivMessageImage)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isCurrentUser) {
            if (message.isImage) VIEW_TYPE_OUTGOING_IMAGE else VIEW_TYPE_OUTGOING
        } else {
            if (message.isImage) VIEW_TYPE_INCOMING_IMAGE else VIEW_TYPE_INCOMING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OUTGOING_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_message_outgoing, parent, false)
                OutgoingImageMessageViewHolder(view)
            }
            VIEW_TYPE_INCOMING_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_message_incoming, parent, false)
                IncomingImageMessageViewHolder(view)
            }
            VIEW_TYPE_OUTGOING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_outgoing, parent, false)
                OutgoingMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_incoming, parent, false)
                IncomingMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when (holder) {
            is OutgoingImageMessageViewHolder -> {
                holder.tvMessageUsername.text = currentMessage.username
                holder.tvMessageHour.text = currentMessage.hour.substring(0, currentMessage.hour.length - 3)
                Glide.with(holder.ivMessageImage.context).load(currentMessage.message).into(holder.ivMessageImage)
            }
            is IncomingImageMessageViewHolder -> {
                holder.tvMessageUsername.text = currentMessage.username
                holder.tvMessageHour.text = currentMessage.hour.substring(0, currentMessage.hour.length - 3)
                Glide.with(holder.ivMessageImage.context).load(currentMessage.message).into(holder.ivMessageImage)
            }
            is OutgoingMessageViewHolder -> {
                holder.tvMessageUsername.text = currentMessage.username
                holder.tvMessageHour.text = currentMessage.hour.substring(0, currentMessage.hour.length - 3)
                holder.tvMessageText.text = currentMessage.message
            }
            is IncomingMessageViewHolder -> {
                holder.tvMessageUsername.text = currentMessage.username
                holder.tvMessageHour.text = currentMessage.hour.substring(0, currentMessage.hour.length - 3)
                holder.tvMessageText.text = currentMessage.message
            }
        }
    }

    override fun getItemCount() = messageList.size

    fun insertNewMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }
}

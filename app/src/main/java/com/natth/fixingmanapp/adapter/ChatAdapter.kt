package com.natth.fixingmanapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.model.Chat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.me_image_list_tiem.view.*
import kotlinx.android.synthetic.main.you_message_list_item.*
import kotlinx.android.synthetic.main.message_list_item.view.*
import kotlinx.android.synthetic.main.you_image_list_item.view.*

class ChatAdapter(private val chat: List<Chat>, private val who:String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_HOLDER_ME: Int = 0
    var VIEW_HOLDER_YOU: Int = 1
    var VIEW_HOLDER_IMAGE_ME :Int = 2
    var VIEW_HOLDER_IMAGE_YOU :Int = 3
    lateinit var  context: Context;
    override fun getItemViewType(position: Int): Int {
        return if (chat!!.get(position).who.equals("tech")and chat!!.get(position).type.equals("Message")) {
            VIEW_HOLDER_ME
        }else if (chat!!.get(position).who.equals("tech")and chat!!.get(position).type.equals("Image")) {
            VIEW_HOLDER_IMAGE_ME
        } else if (chat!!.get(position).who.equals("user")and chat!!.get(position).type.equals("Message")) {
            VIEW_HOLDER_YOU
        }else{
            VIEW_HOLDER_IMAGE_YOU
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {

        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            VIEW_HOLDER_ME -> return ViewHolderMe(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.message_list_item,
                    parent,
                    false
                )
            )
            VIEW_HOLDER_IMAGE_ME -> return ViewHolderMeImage(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.me_image_list_tiem,
                    parent,
                    false
                )
            )

            VIEW_HOLDER_YOU -> return ViewHolderYou(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.you_message_list_item,
                    parent,
                    false
                )
            )
            else  -> return ViewHolderYouImage(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.you_image_list_item,
                    parent,
                    false
                )
            )
        }

        return return viewHolder

    }

    override fun onBindViewHolder(v: RecyclerView.ViewHolder, position: Int) {
        val item = chat[position]
        if (v is ViewHolderMe) { // Handle Image Layout
            val viewHolderMe = v as ViewHolderMe
            viewHolderMe.text_messages!!.setText(String.format("%s",chat!!.get(position).msg))
            viewHolderMe.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            viewHolderMe.itemView.tag = viewHolderMe
        } else if (v is ViewHolderYou) { // Handle Video Layout
            val viewHolderYou = v as ViewHolderYou
            viewHolderYou.text_messages!!.setText(String.format("%s",chat!!.get(position).msg))
            viewHolderYou.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            viewHolderYou.itemView.tag = viewHolderYou
        }else if (v is ViewHolderMeImage){
            val ViewHolderMeImage = v

            Picasso.get()
                .load(chat!!.get(position).msg)
                .resize(50, 50)
                .centerCrop()
                .into(ViewHolderMeImage.image_messages)
            ViewHolderMeImage.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            ViewHolderMeImage.itemView.tag = ViewHolderMeImage
        }else if (v is ViewHolderYouImage){

            val ViewHolderYouImage = v

            Picasso.get()
                .load(chat!!.get(position).msg)
                .resize(50, 50)
                .centerCrop()
                .into(ViewHolderYouImage.image_messages)
            ViewHolderYouImage.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            ViewHolderYouImage.itemView.tag = ViewHolderYouImage

        }

    }

    override fun getItemCount(): Int = chat.size

    inner class ViewHolderMe(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var text_messages: TextView = itemView!!.textView_message_text
        var text_time: TextView = itemView!!.textView_message_time

    }


    inner class ViewHolderYou(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var text_messages= itemView?.findViewById<TextView>(R.id.textView_message_text_you)
        var text_time= itemView?.findViewById<TextView>(R.id.textView_message_time_you)

    }
    inner class ViewHolderMeImage(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val context = itemView!!.context
        var image_messages: ImageView = itemView!!.imageView_message_image_me
        var text_time: TextView = itemView!!.textView_message_time_me

    }
    inner class ViewHolderYouImage(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val context = itemView!!.context
        var image_messages: ImageView = itemView!!.imageView_message_image_you
        var text_time= itemView?.findViewById<TextView>(R.id.textView_message_time_you)

    }
}
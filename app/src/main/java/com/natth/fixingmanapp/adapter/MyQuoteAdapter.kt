package com.natth.fixingmanapp.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.activities.ChatActivity
import com.natth.fixingmanapp.activities.DetailFixActivity
import com.natth.fixingmanapp.model.RequestFix
import kotlinx.android.synthetic.main.myquote_list_item.view.*


class MyQuoteAdapter(private val Quote: List<RequestFix> ,val onItemClicked:(v:View)-> Unit): RecyclerView.Adapter<MyQuoteAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.myquote_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Quote[position]
        if (item.status == "accept") {
            holder.fixId.text = "รายการซ่อมที่ fx-" + item.idRequest.toString()
            holder.nameCus.text = "ชื่อลูกค้า: " + item.firstnameUser + " " + item.lastnameUser

            holder.statusFix.text = "สถานะ: รับซ่อมแล้ว"

            holder.date.text = "เมื่อเวลา: " + item.lastUpdate
            holder.itemView.setOnClickListener { v ->
                val context = v.context
                val intent = Intent(context, DetailFixActivity::class.java)
                intent.putExtra("id_fix",item.idRequest.toString())
                context.startActivity(intent)

                holder.btnNavi.setOnClickListener {

                    val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" +item.user_lat!!.toDouble() + "," +item.user_lon!!.toDouble()+"&mode=d")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context!!.packageManager) != null) {
                        context.startActivity(mapIntent)

                    }
            }



            }
            holder.btnChat.setOnClickListener { v ->
                val context = v.context
                val i  = Intent(context , ChatActivity::class.java)
                i.putExtra("id_fix_chat",item.idRequest.toString())
                context.startActivity(i)

            }

            holder.btnCall.setOnClickListener(onItemClicked)

            holder.btnNavi.setOnClickListener {v->
                val context = v.context
                val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + item.user_lat+ "," +  item.user_lon+"&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)

                }
            }
        }

    }

    override fun getItemCount(): Int = Quote.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var fixId:TextView = itemView.tvListId
        var nameCus:TextView = itemView.tvNameCus
        var statusFix:TextView = itemView.tvStatus
        var date:TextView = itemView.tvDateTime
        var btnNavi: Button = itemView.btnitemNavi
        var btnChat :Button = itemView.btnitemChat
        var btnCall :Button = itemView.btnitemCall

    }


}
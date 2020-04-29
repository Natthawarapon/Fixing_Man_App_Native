package com.natth.fixingmanapp.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.activities.DetailFixActivity
import com.natth.fixingmanapp.model.RequestFix
import kotlinx.android.synthetic.main.myquote_list_item.view.*
import kotlinx.android.synthetic.main.previos_list_item_notdone.view.*
import kotlinx.android.synthetic.main.previous_list_item.view.*

class PreviousListAdapter(private val Plist: List<RequestFix> ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var VIEW_HOLDER_DONE :Int = 0
    var VIEW_HOLDER_NOTDONE:Int = 1

    override fun getItemViewType(position: Int): Int {
        return if(Plist!!.get(position).status.equals("done")){
            VIEW_HOLDER_DONE
        }else if (Plist!!.get(position).status.equals("review")){
            VIEW_HOLDER_DONE
        } else {
            VIEW_HOLDER_NOTDONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder:RecyclerView.ViewHolder = when (viewType){
            VIEW_HOLDER_DONE -> return ViewHolderDone(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.previous_list_item,
                    parent,
                    false
                )
            )
            else -> return ViewHolderNotDone(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.previos_list_item_notdone ,
                    parent ,
                    false
                )
            )
        }


    }


    override fun onBindViewHolder(v: RecyclerView.ViewHolder , position: Int) {

        if ( v is ViewHolderDone){
            val viewHolderDone = v
            var c = v.context
            val status = "ซ่อมสำเร็จ"
            viewHolderDone.fixId!!.setText(String.format("%s","รายการซ่อม :fx-"+Plist!!.get(position).idRequest))
            viewHolderDone.nameCus!!.setText(String.format("%s","ชื่อลูกค้า :"+Plist!!.get(position).firstnameUser +" " +Plist!!.get(position).lastnameUser ))
            viewHolderDone.statusFix!!.setText(String.format("%s",status))
            viewHolderDone.statusFix.setTextColor(Color.parseColor("#008000"))
            viewHolderDone.date!!.setText(String.format("%s","เมื่อเวลา: " +Plist!!.get(position).lastUpdate))

            viewHolderDone.itemView.tag = viewHolderDone
        }else if (v is ViewHolderNotDone){
            val viewHolderNotDone = v
            var status:String? = null
            if (Plist!!.get(position).status == "reject"){
                status = "ปธิเสธการเรียกซ่อม"
            }else if (Plist!!.get(position).status == "cancel"){
                status ="ลูกค้ายกเลิกการทำรายการ"
            }else if (Plist!!.get(position).status == "overtime"){
                status ="หมดเวลาการทำรายการ"
            }else {
                status = "คุณยกเลิกรายการซ่อม"
            }
            viewHolderNotDone.fixId!!.setText(String.format("%s","รายการซ่อม :fx-"+Plist!!.get(position).idRequest))
            viewHolderNotDone.nameCus!!.setText(String.format("%s","ชื่อลูกค้า :"+Plist!!.get(position).firstnameUser +" " +Plist!!.get(position).lastnameUser ))
            viewHolderNotDone.statusFix!!.setText(String.format("%s",status))
            viewHolderNotDone.statusFix.setTextColor(Color.parseColor("#FF0000"))

            viewHolderNotDone.date!!.setText(String.format("%s","เมื่อเวลา: " +Plist!!.get(position).lastUpdate))

            viewHolderNotDone.itemView.tag = viewHolderNotDone

        }
//        val item = Plist[position]
//
//        holder.fixId.text = "รายการซ่อมที่ FX-" + item.idRequest.toString()
//        holder.nameCus.text = "ชื่อลูกค้า: " + item.firstnameUser + " " + item.lastnameUser
//
//        if (item.status == "done" || item.status == "review" ) {
//            holder.statusFix.text = "ซ่อมสำเร็จ"
//            holder.statusFix.setTextColor(Color.parseColor("#008000"))
//        }else if (item.status == "reject"){
//            holder.statusFix.text = "คุณยกเลิกรายการซ่อม"
//            holder.statusFix.setTextColor(Color.parseColor("#FF0000"))
//        }else if (item.status == "dismiss"){
//            holder.statusFix.text = "ร้านซ่อมขอยกเลิกรายการ"
//            holder.statusFix.setTextColor(Color.parseColor("#FF0000"))
//        }else{
//            holder.statusFix.text = "หมดเวลาการทำรายการ"
//            holder.statusFix.setTextColor(Color.parseColor("#FF0000"))
//        }
//        holder.date.text = "เมื่อเวลา: " + item.lastUpdate

//        holder.itemView.setOnClickListener { v ->
//            val context = v.context
//            val intent = Intent(context, DetailFixActivity::class.java)
//            intent.putExtra("id_fix",item.idRequest.toString())
//            context.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int = Plist.size



    inner class ViewHolderDone(itemView: View):RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        var fixId: TextView = itemView.tvListIdP
        var nameCus: TextView = itemView.tvNameCusP
        var statusFix: TextView = itemView.tvStatusP
        var date: TextView = itemView.tvDateTimeP
        var ratingBarList :RatingBar = itemView.ratingBarListFixDone
        var textRating :TextView = itemView.tvRatingListFix
    }
    inner class ViewHolderNotDone(itemView: View):RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        var fixId: TextView = itemView.tvListIdND
        var nameCus: TextView = itemView.tvNameCusND
        var statusFix: TextView = itemView.tvStatusND
        var date: TextView = itemView.tvDateTimeND

    }


}
package com.natth.fixingmanapp.activities

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.ListFragment
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.model.CauseCancelFix
import com.natth.fixingmanapp.model.FixDetail
import com.natth.fixingmanapp.model.HistoryFix
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.activity_detail_current_fix.*
import kotlinx.android.synthetic.main.dialog_fragment.view.*
import kotlinx.android.synthetic.main.dialog_fragment_cause_cancel.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailFixActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    var idFixDetail: String? =null
    var idFixClick:String? = null
    var user_latitud:Double = 0.0
    var user_longitude:Double = 0.0
    val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_current_fix)

        val actionbar = supportActionBar
        actionbar!!.title = "Detail Fix"
        actionbar.setDisplayHomeAsUpEnabled(true)

        idFixDetail = intent.getStringExtra("idFix")
        idFixClick = intent.getStringExtra("id_fix")
        var idF:Int
        if (idFixDetail != null) {
            idF = idFixDetail!!.toInt()
        }else {
            idF = idFixClick!!.toInt()
        }



        api.getrequestFixByID(idF).enqueue(object : Callback<List<FixDetail>> {
            override fun onResponse(call: Call<List<FixDetail>>, response: Response<List<FixDetail>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {

                    if (response.body()!![i].idRequest == idF) {

                        tvFixIDD.text = "รายการซ่อม:fx-${idF}"
                        tvaddressStore.text = "ตำแหน่งของคุณ :${response.body()!![i].nameStore}"
                        tvaddressUser.text ="ตำแหน่งของลูกค้า :${response.body()!![i].user_address}"
                        tvstatusFix.text = "รับซ่อมแล้ว"
                        tvDateFix.text = "เมื่อ : ${(response.body()!![i].lastUpdate).toString()}"
                        user_latitud = (response.body()!![i].user_lat)!!.toDouble()
                        user_longitude = (response.body()!![i].user_lon)!!.toDouble()
                    }

                }
            }

            override fun onFailure(call: Call<List<FixDetail>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })


            btnNavi.setOnClickListener {
                val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" +user_latitud+ "," + user_longitude+"&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(this!!.packageManager) != null) {
                    startActivity(mapIntent)

                }

            }

            btnCall.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            }else {

                startCall()
            }

            }

        btnChat.setOnClickListener {
            var idx = idF.toString()
            val i = Intent(this, ChatActivity::class.java)
            i.putExtra("idFix_chat" , idx)
            startActivity(i)

        }

        btnDone.setOnClickListener {
            val mAlert = AlertDialog.Builder(this@DetailFixActivity)
            mAlert.setCancelable(false)
            mAlert.setTitle("ยืนยันการซ่อมสำเร็จ !")
            mAlert.setMessage("คุณต้องการยืนยันการซ่อมสำเร็จของรายการซ่อมที่ FX-${idF} ใช่หรือไม่")
            mAlert.setPositiveButton("Yes") { dialog, id ->
                val addHistoryFix = HistoryFix()
                addHistoryFix.idRequest = idF
                addHistoryFix.status = "done"
                api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse( call: Call<HashMap<String, String>> , response: Response<HashMap<String, String>> ) {
                        Log.d("postHistoryFix", "success")

                        val refresh = Intent(this@DetailFixActivity, ListFragment::class.java)
                        startActivity(refresh)
                        finish() //
                    }
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        Log.d("postHistoryFix", "fail ${t}")
                    }
                })
            }
            mAlert.setNegativeButton("No") { dialog: DialogInterface?, which: Int ->
                dialog!!.dismiss()
            }

            mAlert.show()
        }
        btnDismiss.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fragment_cause_cancel, null)

            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView).setCancelable(false)

            val mAlertDialog = mBuilder.show()
            mDialogView.btnClose.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.btn1.setOnClickListener {
                val addCauseCancelFix  = CauseCancelFix()
                addCauseCancelFix.idFix = idF
                addCauseCancelFix.cause = "ไปไม่ทันเวลา"
                addCauseCancelFix.status = "dismiss"

                api.addCauseCancelFix(addCauseCancelFix).enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse( call: Call<HashMap<String, String>> , response: Response<HashMap<String, String>> ) {
                        Log.d("postHistoryFix", "success")

                        val refresh = Intent(this@DetailFixActivity, ListFragment::class.java)
                        startActivity(refresh)
                        finish() //
                    }
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        Log.d("postHistoryFix", "fail ${t}")
                    }
                })
                Toast.makeText(this , "ยกเลิกรายการรับซ่อมแล้ว" ,Toast.LENGTH_LONG).show()
            }
            mDialogView.btn2.setOnClickListener {
                val addCauseCancelFix  = CauseCancelFix()
                addCauseCancelFix.idFix = idF
                addCauseCancelFix.cause = "รับงานโดยไม่ตั้งใจ"
                addCauseCancelFix.status = "dismiss"
                api.addCauseCancelFix(addCauseCancelFix).enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse( call: Call<HashMap<String, String>> , response: Response<HashMap<String, String>> ) {
                        Log.d("postHistoryFix", "success")

                        val refresh = Intent(this@DetailFixActivity, ListFragment::class.java)
                        startActivity(refresh)
                        finish() //
                    }
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        Log.d("postHistoryFix", "fail ${t}")
                    }
                })
                Toast.makeText(this , "ยกเลิกรายการรับซ่อมแล้ว" ,Toast.LENGTH_LONG).show()
            }

        }


    }

    private fun startCall() {
        val phoneStore = "0901115099"
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: " +phoneStore)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            return
        }
        startActivity(callIntent)
    }

    override fun onSupportNavigateUp():Boolean {
        onBackPressed()

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) {
            startCall()
        }
    }


}

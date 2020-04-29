package com.natth.fixingmanapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.model.HistoryFix
import com.natth.fixingmanapp.model.RequestFix
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_fragment.*
import kotlinx.android.synthetic.main.dialog_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    var STOP_ALERT = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvNameStore.text = "ชื่อร้าน : PKservice"
        var id = 1
        var sf = 0
        var STOP_LOAD_REQUEST = 0
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                api.getrequestFix(id).enqueue(object : Callback<List<RequestFix>> {
                    override fun onResponse(
                        call: Call<List<RequestFix>>,
                        response: Response<List<RequestFix>>
                    ) {

                        Log.d("GetRequest", "success")
                        for (i in 0..response.body()!!.size - 1) {
                            if (response.body()!![i].status == "request") {
                                Log.d("status", "request")
                                sf = 1
                                STOP_LOAD_REQUEST = 1
                                var nameUser =
                                    response.body()!![i].firstnameUser + " " + response.body()!![i].lastnameUser
                                var Idrequest = response.body()!![i].idRequest
                                var dateTime = (response.body()!![i].lastUpdate).toString()
                                showDialogRequest(nameUser, Idrequest, dateTime)
                            }
                            if (sf == 1) break
                        }

                    }

                    override fun onFailure(call: Call<List<RequestFix>>, t: Throwable) {
                        Log.d("GetRequest", "fail ${t}")
                    }
                })
                if (STOP_LOAD_REQUEST == 1) {
                    handler.removeCallbacks(this)
                } else {
                    handler.postDelayed(this, 1000)//1 sec delay
                }

            }
        }, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun showDialogRequest(nameUser: String, id: Int, dateTime: String) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fragment, null)

        mDialogView.dialogTitle.text = "รายการเรียกซ่อม !"
        mDialogView.tvIDFix.text = "รหัสรายการเรียกซ่อม : RF-${id}"
        mDialogView.dialogMessage.text = "มีการรายการเรียกซ่อมจาก ${nameUser} "
        mDialogView.tvDateTime.text = "เมื่อ : ${dateTime}"
        mDialogView.btnreject.text = "ปธิเสธ"
        mDialogView.btnaccept.text = "รับซ่อม"
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView).setCancelable(false)

        val mAlertDialog = mBuilder.show()

        // technicians reject request_fix  status == reject
        mDialogView.btnreject.setOnClickListener {

            mAlertDialog.dismiss()
            val addHistoryFix = HistoryFix()
            addHistoryFix.idRequest = id
            addHistoryFix.status = "reject"
            api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    Log.d("postHistoryFix", "success")

                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    Log.d("postHistoryFix", "fail ${t}")

                }
            })
            rejectRequest()
        }

        mAlertDialog.btnaccept.setOnClickListener {
            mAlertDialog.dismiss()
            val addHistoryFix = HistoryFix()
            addHistoryFix.idRequest = id
            addHistoryFix.status = "accept"
            // technicians accept request_fix  status == accept
            api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {
                    Log.d("postHistoryFix", "success")
                    //เรียกหน้า google map Navigator
                }
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    Log.d("postHistoryFix", "fail ${t}")

                }
            })
            acceptRequest()
        }

        var STOP_LOAD_REQUEST = 0
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                api.getrequestFix(1).enqueue(object : Callback<List<RequestFix>> {
                    override fun onResponse(call: Call<List<RequestFix>>, response: Response<List<RequestFix>>) {

                        Log.d("GetRequestSS", "success")
                        for (i in 0..response.body()!!.size-1) {
                            if (response.body()!![i].idRequest == id) {
                                if (response.body()!![i].status == "overtime") {
                                    STOP_LOAD_REQUEST = 1
                                    mAlertDialog.dismiss()
                                    var msg = "overtime"
                                    Log.d("aa", "overtime")
                                    overTimeRequestFix(msg)
                                } else if (response.body()!![i].status == "cancel") {
                                    STOP_LOAD_REQUEST = 1
                                    mAlertDialog.dismiss()
                                    var msg = "cancel"
                                    Log.d("aa", "cancel")
                                    overTimeRequestFix(msg)
                                }
                            }
                            if (STOP_LOAD_REQUEST == 1) break
                        }

                    }

                    override fun onFailure(call: Call<List<RequestFix>>, t: Throwable) {
                        Log.d("GetRequestSS", "fail ${t}")
                    }
                })
                if (STOP_LOAD_REQUEST == 1) {
                    handler.removeCallbacks(this)
                } else {
                    handler.postDelayed(this, 1000)//1 sec delay
                }
            }
        }, 0)
    }


    fun overTimeRequestFix(msg: String) {
        val mAlert = AlertDialog.Builder(this@MainActivity)
        mAlert.setCancelable(false)
        if (msg == "overtime") {
            mAlert.setTitle("รายการเรียกซ่อมถูกยกเลิก !")
            mAlert.setMessage("ขออภัย การทำรายการเกินกว่าเวลาที่กำหนด")
        } else if (msg == "cancel") {
            mAlert.setTitle("รายการเรียกซ่อมถูกยกเลิก !")
            mAlert.setMessage("ขออภัย ผู้ใช้ได้ทำการยกเลิกรายการเรียกซ่อม")
        }

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
            STOP_ALERT = 1
        }

        mAlert.show()

    }


    fun acceptRequest() {
        val mAlert = AlertDialog.Builder(this@MainActivity)
        mAlert.setCancelable(false)
        mAlert.setTitle("รายการเรียกซ่อม !")
        mAlert.setMessage("รับซ่อมเรียบร้อยแล้ว")

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
            STOP_ALERT = 1

            val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + 13.7031231+ "," + 100.5414076+"&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(this@MainActivity.packageManager) != null) {
                startActivity(mapIntent)

            }
        }

        mAlert.show()

    }

    fun rejectRequest() {
        val mAlert = AlertDialog.Builder(this@MainActivity)
        mAlert.setCancelable(false)
        mAlert.setTitle("รายการเรียกซ่อม !")
        mAlert.setMessage("ปฏิเสธการเรียกซ่อมเรียบร้อยแล้ว")

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
            STOP_ALERT = 1
        }

        mAlert.show()

    }
}

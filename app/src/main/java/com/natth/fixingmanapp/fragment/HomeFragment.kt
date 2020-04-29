package com.natth.fixingmanapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.maps.android.SphericalUtil
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.activities.DetailFixActivity
import com.natth.fixingmanapp.model.HistoryFix
import com.natth.fixingmanapp.model.RequestFix
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.dialog_fragment.*
import kotlinx.android.synthetic.main.dialog_fragment.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    var STOP_ALERT = 0

    private val TOPIC = "test"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(context as Activity, R.layout.fragment_home)
        FirebaseMessaging.getInstance().subscribeToTopic("test")
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnSuccessListener(context as Activity , { instanceIdResult ->
                val newToken: String = instanceIdResult.getToken()
                Log.e("newToken", newToken)
            })
//        btn_subscribe.setOnClickListener {
//            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
//            Toast.makeText(context, "Subscribe Topic: " + TOPIC, Toast.LENGTH_SHORT).show()
//        }
//        switch1.setOnCheckedChangeListener { compoundButton, onSwitch ->
//            if (onSwitch){
//                tvStatus.text = "คุณกำลังเปิดรับการเรียกซ่อม..."
//                tvStatus.setTextColor(Color.parseColor("#008000"))
//
//            }else{
//                tvStatus.text = "คุณปิดรับการเรียกซ่อม..."
//                tvStatus.setTextColor(Color.parseColor("#FF0000"))
//            }
//
//        }


      textView2.text = "ชื่อร้าน : PKservice"
        var id = 1
        var sf = 0
        var STOP_LOAD_REQUEST = 0
        val handler = Handler()
        handler.postDelayed(object : Runnable { override fun run() {
            api.getrequestFix(id).enqueue(object : Callback<List<RequestFix>> {
                    override fun onResponse(call: Call<List<RequestFix>>, response: Response<List<RequestFix>>) {

                        Log.d("GetRequest", "success")
                        for (i in 0..response.body()!!.size - 1) {
                            if (response.body()!![i].status == "request") {
                                Log.d("statussss", "request")
                                sf = 1
                                STOP_LOAD_REQUEST = 1
                                var nameUser =
                                    response.body()!![i].firstnameUser + " " + response.body()!![i].lastnameUser
                                var Idrequest = response.body()!![i].idRequest
                                var dateTime = (response.body()!![i].lastUpdate).toString()
                                var user_lat = (response.body()!![i].user_lat).toString()
                                var user_lon = (response.body()!![i].user_lon ).toString()
                                var tech_lat = (response.body()!![i].tech_lat ).toString()
                                var tech_lon = (response.body()!![i].tech_lon ).toString()
                                showDialogRequest(nameUser, Idrequest, dateTime , user_lat ,user_lon ,tech_lat ,tech_lon)
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

    fun showDialogRequest(nameUser: String, id: Int, dateTime: String ,user_lat:String ,user_lon:String ,tech_lat:String ,tech_lon:String) {
        var idFix = id
        val from = LatLng(user_lat.toDouble(), user_lon.toDouble())
        val to = LatLng(tech_lat.toDouble(), tech_lon.toDouble())

        //Calculating the distance in meters
        //Calculating the distance in meters

        val distance: Double = SphericalUtil.computeDistanceBetween(from, to)
        var km_distance = (distance / 1000)
        val decimal_Km = BigDecimal(km_distance).setScale(2 ,RoundingMode.HALF_EVEN)

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment, null)

        mDialogView.dialogTitle.text = "รายการเรียกซ่อม !"
        mDialogView.tvIDFix.text = "FX-${idFix}"
        mDialogView.dialogMessage.text = "มีการรายการเรียกซ่อมจาก ${nameUser} "
        mDialogView.tvDateTime.text = "เมื่อ : ${dateTime}"
        mDialogView.tvDistance.text = "ระยะทาง : ${decimal_Km}/Km"
        mDialogView.btnreject.text = "ปธิเสธ"
        mDialogView.btnaccept.text = "รับซ่อม"
        val mBuilder = AlertDialog.Builder(context)
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
            acceptRequest(idFix)

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
        val mAlert = AlertDialog.Builder(context)
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


    fun acceptRequest(id:Int) {
        var x = id.toString()
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        mAlert.setTitle("รายการเรียกซ่อม !")
        mAlert.setMessage("รับซ่อมเรียบร้อยแล้ว")

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
            STOP_ALERT = 1
            val i = Intent(activity, DetailFixActivity::class.java)
            i.putExtra("idFix" , x)
            startActivity(i)

        }

        mAlert.show()

    }

    fun rejectRequest() {
        val mAlert = AlertDialog.Builder(context)
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

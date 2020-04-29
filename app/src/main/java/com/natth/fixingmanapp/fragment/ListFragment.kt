package com.natth.fixingmanapp.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager

import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.activities.DetailFixActivity
import com.natth.fixingmanapp.activities.PreviousListActivity
import com.natth.fixingmanapp.adapter.MyQuoteAdapter
import com.natth.fixingmanapp.model.FixDetail
import com.natth.fixingmanapp.model.RequestFix
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    val REQUEST_PHONE_CALL = 1

    private lateinit var adapter: MyQuoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//
//
//
//    }
        var items:List<RequestFix>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if ((requestCode == 1001 ) && resultCode == Activity.RESULT_OK){
//            val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
//            ft.detach(this@ListFragment).attach(this@ListFragment).commit()
//
//        }
//
//    }

    override fun onResume() {
        super.onResume()

        var id = 1
        var sf = 0
        val listarray =arrayListOf<RequestFix>()
        api.getrequestFix(id).enqueue(object : Callback<List<RequestFix>> {
            override fun onResponse(call: Call<List<RequestFix>>, response: Response<List<RequestFix>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (response.body()!![i].status == "accept") {
                        Log.d("list99" , "${response.body()!!}")
                        val rf = RequestFix()
                        rf.idTechnician = response.body()!![i].idTechnician
                        rf.nameStore = response.body()!![i].nameStore
                        rf.idUser = response.body()!![i].idUser
                        rf.firstnameUser = response.body()!![i].firstnameUser
                        rf.lastnameUser = response.body()!![i].lastnameUser
                        rf.idRequest =  response.body()!![i].idRequest
                        rf.lastUpdate = response.body()!![i].lastUpdate
                        rf.status = response.body()!![i].status
                        rf.user_lat = response.body()!![i].user_lat
                        rf.user_lon = response.body()!![i].user_lon
                        rf.tech_lat = response.body()!![i].tech_lat
                        rf.tech_lon = response.body()!![i].tech_lon
                        rf.user_address = response.body()!![i].user_address

                        listarray.add(rf)

                        list.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = MyQuoteAdapter(listarray){
                                if(ActivityCompat.checkSelfPermission(context , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(context as Activity, arrayOf(
                                        Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
                                }else {

                                    startCall()
                                }


                            }

                        }

                    }
                }
                for (item in  listarray){
                    Log.d("list88" , "${item}")
                }

            }

            override fun onFailure(call: Call<List<RequestFix>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })



        oldList.setOnClickListener {
            val i = Intent(activity, PreviousListActivity::class.java)
            startActivity(i)
        }
    }
    private fun startCall() {
        val phoneStore = "0901115099"
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: " +phoneStore)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            return
        }
        startActivity(callIntent)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) {
            startCall()
        }
    }
}



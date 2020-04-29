package com.natth.fixingmanapp.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.adapter.MyQuoteAdapter
import com.natth.fixingmanapp.adapter.PreviousListAdapter
import com.natth.fixingmanapp.model.RequestFix
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.activity_previous_list.*
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PreviousListActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    var id = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_list)

        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)


        var click = 0

        btnListDone.setOnClickListener {
            click = 1
            ListFix(click)
            btnListDone.setBackgroundColor(Color.parseColor("#33FF86"))
          btnListAll.setBackgroundColor(Color.parseColor("#D9E2DD"))
            btnNotDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
        }
        btnListAll.setOnClickListener {
            click = 0
            ListFix(click)
            btnListAll.setBackgroundColor(Color.parseColor("#33FF86"))
            btnListDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
            btnNotDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
        }
        btnNotDone.setOnClickListener {
            click = 2
            ListFix(click)
            btnListAll.setBackgroundColor(Color.parseColor("#D9E2DD"))
            btnListDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
            btnNotDone.setBackgroundColor(Color.parseColor("#33FF86"))
        }
        if ( click == 0 ){
            ListFix(0)
            btnListAll.setBackgroundColor(Color.parseColor("#33FF86"))
            btnListDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
            btnNotDone.setBackgroundColor(Color.parseColor("#D9E2DD"))
        }

    }

    fun  ListFix (click:Int){
        val listarray =arrayListOf<RequestFix>()
        api.getrequestFix(id).enqueue(object : Callback<List<RequestFix>> {
            override fun onResponse(call: Call<List<RequestFix>>, response: Response<List<RequestFix>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (click == 1){
                        if (response.body()!![i].status == "done" || response.body()!![i].status == "review") {
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

                            Prelist.apply {
                                layoutManager = LinearLayoutManager(this@PreviousListActivity)
                                adapter = PreviousListAdapter(listarray)
                            }

                        }
                    }else if (click == 2 ){
                        if (response.body()!![i].status != "accept" && response.body()!![i].status != "done" && response.body()!![i].status != "review" ) {
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

                            Prelist.apply {
                                layoutManager = LinearLayoutManager(this@PreviousListActivity)
                                adapter = PreviousListAdapter(listarray)
                            }

                        }
                    }else{
                        if (response.body()!![i].status != "accept") {
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

                            Prelist.apply {
                                layoutManager = LinearLayoutManager(this@PreviousListActivity)
                                adapter = PreviousListAdapter(listarray)
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

    }
    override fun onSupportNavigateUp():Boolean {
        onBackPressed()
        return true
    }
}

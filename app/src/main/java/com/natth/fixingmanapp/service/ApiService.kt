package com.natth.fixingmanapp.service

import com.natth.fixingmanapp.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("technicain")
    fun  createTechnicain(@Body addTechnician: Technician): Call<HashMap<String,String>>

    @GET("requestfixbytech/{id}")
    fun getrequestFix(@Path("id")id:Int):Call<List<RequestFix>>

    @POST(value = "change_status_fix" )
    fun changeStatusFix(@Body addHistoryFix: HistoryFix):Call<HashMap<String,String>>

    @GET("requestfixbyidFix/{id}")
    fun getrequestFixByID(@Path("id")id:Int):Call<List<FixDetail>>

    @POST("cancelFix")
    fun addCauseCancelFix (@Body addCauseCancelFix : CauseCancelFix ) : Call <HashMap<String,String>>
}
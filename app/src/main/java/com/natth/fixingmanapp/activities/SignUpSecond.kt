package com.natth.fixingmanapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.model.Technician
import com.natth.fixingmanapp.service.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signup2.etAddress
import kotlinx.android.synthetic.main.activity_signup2.etStore_Name
import kotlinx.android.synthetic.main.activity_signup2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignUpSecond : AppCompatActivity() {
    private  val PERMISSION_REQUEST =10
    lateinit var  locationManager: LocationManager
    private var hasGps =false
    private var  hasNetwork = false
    private  var locationGps:Location? = null
    private  var locationNetwork:Location? = null
   private var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create()
        )
        .build()
    val api =retrofit.create(ApiService::class.java)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)




        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permission)){
                enableView()
            }else{

                requestPermissions(permission, PERMISSION_REQUEST)
            }
        }else {
            enableView()

        }


//        Toast.makeText(this@SignUp_2, "${name_own}", Toast.LENGTH_LONG)
//            .show()

    }

    private fun disableView(){
        btn_GetLocation.isEnabled =false
        btn_GetLocation.alpha =0.5F
    }
    private fun enableView(){
        btn_GetLocation.isEnabled =true
        btn_GetLocation.alpha =1F
        btn_GetLocation.setOnClickListener { getLocation() }
        Toast.makeText(this ,"done",Toast.LENGTH_LONG).show()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        locationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork =locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork){
            if (hasGps) {
                Log.d("CodeAndroidLocatio", "hasGps")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    object :
                        LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            if (location != null) {
                                locationGps = location
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {

                        }

                        override fun onProviderEnabled(provider: String?) {

                        }

                        override fun onProviderDisabled(provider: String?) {

                        }

                    })

                var localGpsLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    locationGps = localGpsLocation }

            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocatio", "hasGps")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    object :
                        LocationListener{
                        override fun onLocationChanged(location: Location?) {
                            if (location != null) {
                                locationNetwork = location
                                tvLatitude.setText((locationNetwork!!.latitude).toString())
                                tvLongitude.setText((locationNetwork!!.longitude.toString()))
                                Log.d("CodeAndroidLocation" ,"Natwork lat :${locationNetwork!!.latitude} ")
                                Log.d("CodeAndroidLocation" ,"Natwork lon :${locationNetwork!!.longitude} ")
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {

                        }

                        override fun onProviderEnabled(provider: String?) {

                        }

                        override fun onProviderDisabled(provider: String?) {

                        }

                    })

                var localNetworkLocation =   locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation !=null){
                    locationNetwork = localNetworkLocation

                }

            }

            if (locationGps!=null && locationNetwork!= null){
                if (locationGps!!.accuracy > locationNetwork!!.accuracy){
                    Log.d("CodeAndroidLocation" ,"Natwork lat :${locationNetwork!!.latitude} ")
                    Log.d("CodeAndroidLocation" ,"Natwork lon :${locationNetwork!!.longitude} ")
                }else{
                    Log.d("CodeAndroidLocation" ,"GPS lat :${locationGps!!.latitude} ")
                    Log.d("CodeAndroidLocation" ,"GPS lat :${locationGps!!.longitude} ")

                }

            }
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
     fun checkPermission(permissionArray: Array<String>):Boolean{
        var allSucces =true
        for (i in permissionArray.indices){
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSucces =false
        }
        return allSucces
    }
    fun signup(view: View) {
        val bundle = intent.extras
        var data = bundle?.get("data") as Technician

        var  name_own_text = data.nameOwn
       var email_text = data.email
       var phone_text = data.numberphone
       var password_text = data.password

        val addTechnician = Technician()
        addTechnician.nameStore = etStore_Name.text.toString()
        addTechnician.nameOwn =name_own_text
        addTechnician.email =email_text
        addTechnician.numberphone = phone_text
        addTechnician.latitude = tvLatitude.text.toString()
        addTechnician.longitude =tvLongitude.text.toString()
        addTechnician.password = password_text
        addTechnician.address = etAddress.text.toString()

        api.createTechnicain(addTechnician).enqueue(object : Callback<HashMap<String,String>>{


            override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {
                Log.d("postTech" , "success")
                Toast.makeText(this@SignUpSecond, "add success", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {

                Log.d("postTech" , "fail ${t}")

                Toast.makeText(this@SignUpSecond, "add fail", Toast.LENGTH_LONG)
                    .show()
            }

        })

    }
}

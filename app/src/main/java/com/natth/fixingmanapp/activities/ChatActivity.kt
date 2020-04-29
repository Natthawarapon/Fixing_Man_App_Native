package com.natth.fixingmanapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.natth.fixingmanapp.BuildConfig
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.adapter.ChatAdapter
import com.natth.fixingmanapp.model.Chat

import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ChatActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var docIdByFixId :String? = null
    var docByFixIdcash :String? = null
    var idFixDetail: String? =null
    var idFixClick:String? = null
    val listarray =arrayListOf<Chat>()

    var nameImage:String? =null
    val  storage = Firebase.storage
    val storageRef = storage.reference
    private lateinit var mCurrentPhotoPath: String

    private  val IMAGE_CAPTURE_CODE = 1001
    private  val PERMISSION_CODE = 100

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAv97pifQ:APA91bH5W3yCWTjp-C87O5-PD2EPbDMN6IqvwABq0-VtXDT8LnLSe-_E2W6DWDqOs7cZnysXDwnjyKUjaSe_LFzXtSO1uKBvj4UJ4VHkRleMTACqTSTcLmYxSQNc53eVUE2MW0dXR4F4"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        idFixDetail = intent.getStringExtra("idFix_chat")
        idFixClick = intent.getStringExtra("id_fix_chat")

        if (idFixDetail != null) {
            docByFixIdcash = idFixDetail
        }else {
            docByFixIdcash = idFixClick
        }
        var x :String = "184"
        docByFixIdcash  =x
        loadMessage(docByFixIdcash!!)
        imageView_send.setOnClickListener {
            var text = editText_message.text.toString()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = current.format(formatter)


            val docref = db.collection("message")



            if (text.isNotEmpty() ) {
                if (docByFixIdcash != null) {
                    val messages = hashMapOf(
                        "who" to "tech" ,
                        "msg" to text ,
                        "dateTime" to dateTime,
                        "type" to "Message"
                    )
                    val docRef = db.collection("messages").document(docByFixIdcash!!).collection("msg")
                    docRef.add(messages)
                        .addOnSuccessListener { documentReference ->
                            Log.d("TAG", "DocumentSnapshot successfully written!")
                            editText_message.getText()?.clear();
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }


                } else {
                    Toast.makeText(this, "การส่งข้อความไม่สำเร้จ", Toast.LENGTH_LONG).show()
                }


                val topic = "fAAtPqD83Hc:APA91bEYljyZCBRZJ_NXtbA82un17OoOzHaS1KknsS-DQ4xJFxLwD6_Xe7FOHzVjLFhZKVhFOlh7rexbZ86OkU_vkRbESNe8nvLGzndf4-CJ2pLaxhNR9fvSQbBOw2mwLJ2ijEYEgnUD" //topic has to match what the receiver subscribed to

                val notification = JSONObject()
                val notifcationBody = JSONObject()

                try {
                    notifcationBody.put("title", "There are new messages !!")
                    notifcationBody.put("message", text)

                    notification.put("to", topic)
                    notification.put("data", notifcationBody)

                    Log.e("TAG", "try")
                } catch (e: JSONException) {
                    Log.e("TAG", "onCreate: " + e.message)
                }

                sendNotification(notification)

            }else{
                Toast.makeText(this, "กรุณาพิมพ์ข้อความคะ ", Toast.LENGTH_LONG).show()
            }


        }

        fab_send_image.setOnClickListener {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    ) {
                        var permission = arrayOf(Manifest.permission.CAMERA ,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission ,PERMISSION_CODE)

                    }else{
                        openCamera()
                    }


    }
    }
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
//                msg.setText("")
            }, Response.ErrorListener  {
                Toast.makeText(this@ChatActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    private  fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        nameImage = imageFileName
        val storageDir =File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ),"Camera"
        )
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = "file" +image.absolutePath
    return image
    }

    fun openCamera() {
        var FilePhoto :File? = null
        val cameraIntent =Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        FilePhoto =createImageFile()
        if (FilePhoto != null ){
            val photoURI = FileProvider.getUriForFile(
                this@ChatActivity ,
                BuildConfig.APPLICATION_ID +".provider",
                createTempFile()!!
            )
            Toast.makeText(this ,"$photoURI" ,Toast.LENGTH_LONG).show()
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT ,photoURI)
            startActivityForResult(cameraIntent ,IMAGE_CAPTURE_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val  dateTime1 = current.format(formatter)

        if (requestCode == IMAGE_CAPTURE_CODE){
            if (resultCode == Activity.RESULT_OK){
                val imageUri = Uri.parse(mCurrentPhotoPath)
                val file = File(imageUri.path)
                try{
                    val ims :InputStream =FileInputStream(file)
                    val stream = FileInputStream(file)
                    val ImageRef =storageRef.child("$docByFixIdcash/$nameImage.jpg")
                    var  uploadTask = ImageRef.putStream(stream)
                    uploadTask.addOnFailureListener{
                        
                    }.addOnSuccessListener { 
                        val result = it.metadata!!.reference!!.downloadUrl
                        result.addOnSuccessListener { 
                            val imageLink = it.toString()
                            if (docByFixIdcash != null ){
                                val messages = hashMapOf(
                                    "who" to "tech" ,
                                    "msg" to imageLink ,
                                    "dateTime" to dateTime1,
                                    "type" to "Image"
                                )
                                val docRef = db.collection("messages").document(docByFixIdcash!!).collection("msg")
                                docRef.add(messages)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d("TAG", "DocumentSnapshot successfully written!")
                                        editText_message.getText()?.clear();
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("TAG", "Error adding document", e)
                                    }
                            }
                            Toast.makeText(this ,"Upload $nameImage.jpg Successful" ,Toast.LENGTH_LONG).show()
                        }
                    }
                    var x = BitmapFactory.decodeStream(ims)

                }catch (e : FileNotFoundException){
                    return e.printStackTrace()
                }
                
                MediaScannerConnection.scanFile(this@ChatActivity ,
                arrayOf(imageUri.path),
                null
                ){path, uri ->  }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && 
                        grantResults[0] == 
                        PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else {
                    Toast.makeText(this , "Permission denied" ,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    fun  loadMessage(id:String) {

        val doc = db.collection("messages").document(id!!).collection("msg").orderBy("dateTime")
        doc.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                    val c = Chat()
                var who :String
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        c.msg = dc.document.data.getValue("msg") as String?
                        c.dateTime = dc.document.data.getValue("dateTime") as String?
                        c.who = dc.document.data.getValue("who") as String?
                        c.type = dc.document.data.getValue("type") as String?
                        who = dc.document.data.getValue("who") as String
                        listarray.add(c)
                        recycler_view_message.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ChatAdapter(listarray ,who)
                        }
                        Log.d("TAG1", "New city: ${dc.document.data}")
                    }
                }
            }
        }

    }





}

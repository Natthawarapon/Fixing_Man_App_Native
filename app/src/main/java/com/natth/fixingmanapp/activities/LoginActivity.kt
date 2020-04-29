package com.natth.fixingmanapp.activities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.natth.fixingmanapp.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.etPassword

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignIn.setOnClickListener {

            var emailEditText = email2.text.toString()
            var password = Password2.text.toString()
            var go :Int = 1

            var email: String? = null
            var emailFormat: Boolean = Patterns.EMAIL_ADDRESS.toRegex().matches(emailEditText)
            if (emailEditText.isEmpty()) {
                email2.error = "email should not be blank !"
                go=0
            } else {
                if (emailFormat == true) {
                    email = emailEditText
                } else {
                    email2.error = "format email not correct !"
                    go=0
                }
            }
            if (emailEditText == "pks@hotmail.com" &&  password == "12345678"){

            }else{
                if (emailEditText != "pks@hotmail.com" ){
                    Toast.makeText(this ,"อีเมลไม่ถูกต้อง" ,Toast.LENGTH_LONG).show()
                }
                if(password != "12345678"){
                    Toast.makeText(this ,"รหัสผ่านไม่ถูกต้อง" ,Toast.LENGTH_LONG).show()
                }
            }
        }
        btnsignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }








}

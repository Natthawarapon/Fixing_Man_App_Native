package com.natth.fixingmanapp.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.natth.fixingmanapp.R
import com.natth.fixingmanapp.model.Technician
import kotlinx.android.synthetic.main.activity_signup.email1
import kotlinx.android.synthetic.main.activity_signup.etPassword
import kotlinx.android.synthetic.main.activity_signup.etPhone
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.regex.Matcher
import java.util.regex.Pattern



class SignUp : AppCompatActivity() {
    private  val PERMISSION_REQUEST =10
    private var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        requestPermissions(permission, PERMISSION_REQUEST)
        val actionbar = supportActionBar
        actionbar!!.title = "Sign Up"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    fun next(view: View) {
        val intent = Intent(this, SignUpSecond::class.java)

        var firstname = name1.text.toString()
        var lastname = name2.text.toString()
        var emailEditText = email1.text.toString()
        var phone = etPhone.text.toString()
        var password = etPassword.text.toString()
        var go :Int = 1
        if (firstname.isEmpty()) {
            email1.error = "first name should not be blank !"
            go=0
        }
        if (lastname.isEmpty()){
            name2.error = "last name should not be blank !"
            go=0
        }

        var email: String? = null
        var emailFormat: Boolean = Patterns.EMAIL_ADDRESS.toRegex().matches(emailEditText)
        if (emailEditText.isEmpty()) {
            email1.error = "email should not be blank !"
            go=0
        } else {
            if (emailFormat == true) {
                email = emailEditText
            } else {
                email1.error = "format email not correct !"
                go=0
            }
        }
        var tel:String? = null
//        var phoneFormat:Boolean =    Patterns.PHONE.toRegex().matches(phone)

        val p: Pattern = Pattern.compile("(0/66)?[0-9][0-9]{9}")
        val m: Matcher = p.matcher(phone)
      var phoneee:Boolean =  m.find() && m.group().equals(phone)

        if (phone.isEmpty()) {
            etPhone.error = "number phone should not be blank !"
            go=0
        }else {
            if (phoneee == true) {
                tel = phone
            } else {
                etPhone.error = "format numberphone not correct !"
            }
        }
        if (password.isEmpty()){
            etPassword.error ="password should not be blank !"
            go=0
        }

        if (go==0){}else {
            var a = Technician()
            a.password
            var name_own = firstname + " " + lastname
            intent.putExtra("data", Technician(0,null,name_own,tel,email,null,null,password))
            startActivity(intent)
        }
    }
    override fun onSupportNavigateUp():Boolean {
        onBackPressed()

        return true
    }
}


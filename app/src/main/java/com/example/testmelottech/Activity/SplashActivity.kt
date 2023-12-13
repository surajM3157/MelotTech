package com.example.testmelottech.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.testmelottech.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1500
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        /*Handler().postDelayed({
            if (auth?.currentUser != null) {
                val userIntent = Intent(this, UserActivity::class.java)
                startActivity(userIntent)
            } else {
                val signUpIntent = Intent(this, SignUpActivity::class.java)
                startActivity(signUpIntent)
            }
            finish()
        }, SPLASH_DELAY)*/

        Handler().postDelayed({
            val mainIntent = Intent(this, PaymentActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DELAY)
    }
}

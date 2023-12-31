package com.example.testmelottech.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testmelottech.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editUserId: EditText
    private lateinit var editFullName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        editUserId = findViewById(R.id.editUserId)
        editFullName = findViewById(R.id.editFullName)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)
        editEmail = findViewById(R.id.editEmail)
        editPhoneNumber = findViewById(R.id.editPhoneNumber)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnLogin = findViewById(R.id.btnLogin)

        btnSubmit.setOnClickListener {
            val userName = editFullName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val confirmPassword = editConfirmPassword.text.toString()

            if (isValidInput(userName, email, password, confirmPassword)) {
                registerUser(userName, email, password)
            }
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidInput(
        userName: String, email: String, password: String, confirmPassword: String
    ): Boolean {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                confirmPassword
            )
        ) {
            Toast.makeText(applicationContext, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(applicationContext, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        val phoneNumber = editPhoneNumber.text.toString().trim()
        if (phoneNumber.length < 10 || !phoneNumber.matches("\\d+".toRegex())) {
            Toast.makeText(applicationContext, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(
                applicationContext,
                "Password must be at least 6 characters long",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                val userId: String = user!!.uid

                databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["userId"] = userId
                hashMap["userName"] = userName
                hashMap["profileImage"] = ""

                databaseReference.setValue(hashMap).addOnCompleteListener(this) { innerTask ->
                    if (innerTask.isSuccessful) {
                        editFullName.setText("")
                        editEmail.setText("")
                        editPassword.setText("")
                        editConfirmPassword.setText("")
                        val intent = Intent(this, PaymentActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("Firebase", "Registration failed: ${innerTask.exception}")
                    }
                }
            } else {
                Toast.makeText(applicationContext, "User already exists", Toast.LENGTH_SHORT).show()
            }

        }
    }
}

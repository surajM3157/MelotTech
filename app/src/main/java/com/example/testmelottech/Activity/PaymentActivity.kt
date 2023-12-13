package com.example.testmelottech.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testmelottech.R
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class PaymentActivity : AppCompatActivity() {

    private val B2B_PG_REQUEST_CODE = 1
    lateinit var button: Button
    var apiEndPoint = "/pg/v1/pay"
    val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_phone_pay)
        button = findViewById(R.id.button)


        PhonePe.init(this)
        var data = JSONObject()
        data.put("merchantTransactionId", System.currentTimeMillis().toString())
        data.put("merchantId", "PGTESTPAYUAT")
        data.put("merchantUserId", System.currentTimeMillis().toString())
        data.put("amount", 300)
        data.put("mobileNumber", "8888888888")
        data.put("callbackUrl", "https://webhook.site/e357e687-d477-456a-91f1-d23f3dae61ba")

        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type", " PAY_PAGE")
        data.put("PaymentInstrument", mPaymentInstrument)

        val base64: String = android.util.Base64.encodeToString(
            data.toString().toByteArray(
                Charset.defaultCharset()
            ), Base64.NO_WRAP
        )

        val checkSum = sha256(base64 + apiEndPoint + salt) + "###1"
        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(base64)
            .setChecksum(checkSum)
            .setUrl(apiEndPoint)
            .build()


        button.setOnClickListener {
            try {
                startActivityForResult(
                    PhonePe.getImplicitIntent(
                        this, b2BPGRequest, ""
                    )!!, B2B_PG_REQUEST_CODE
                );
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "onCreate:${e.printStackTrace()} ")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = StringBuilder()
        if (requestCode == B2B_PG_REQUEST_CODE) {
            Toast.makeText(this, "check Call back", Toast.LENGTH_SHORT).show()

            if (resultCode != RESULT_CANCELED) {
                Log.i("onActivityResult", "Result Cancelled")
            } else {
                if (data != null && data.extras != null && data.extras!!.keySet().size > 0) {
                    for (key in data.extras!!.keySet()) {
                        result.append(key).append(" = ").append(data.extras!![key]).append("\n")
                    }
                    Log.i("onActivityResult", "result: $result")
                }
            }
        }
    }

    fun sha256(input: String): String {
        val byte = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(byte)
        return digest.fold("") { str, it -> str + "%02x".format(it) }

    }
}
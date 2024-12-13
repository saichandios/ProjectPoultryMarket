package com.gsggroups.poultrymarket

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Employement.EmployementDashboard
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.TransactionRequestBuilder
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.security.MessageDigest


class WelcomeActivity : AppCompatActivity() {
    private lateinit var paymentLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val DEBIT_REQUEST_CODE = 1001 // Request code for transaction
        const val TRANSACTION_RESPONSE = "transaction_response" // Key for response
        const val ERROR_RESPONSE = "error_response" // Key for error response
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if the user is signed in
        val isSignedIn = SharedPreferencesManager.getSignedIn(this)
        if (isSignedIn) {
            startActivity(Intent(this, Dashboard::class.java))
            finish() // Close MainActivity
        } else {
            setContentView(R.layout.activity_welcome)
        }
        // Get references to the cards
        val cardFarmer = findViewById<CardView>(R.id.cardFarmer1)
        val cardTrader = findViewById<CardView>(R.id.cardTrader)
        val cardShopkeeper = findViewById<CardView>(R.id.cardShopkeeper)
        val cardEmployement = findViewById<CardView>(R.id.cardEmployement)
        val ratesCapsule = findViewById<FrameLayout>(R.id.rate_card)

        setupPaymentInputs()
        // Set click listeners for each card
        cardFarmer.setOnClickListener {
            Toast.makeText(this, "Farmer Card Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterSingup::class.java)
            val userRole = "Farmer" // This value would be determined dynamically
            SharedPreferencesManager.saveUserRole(context = this, role = userRole)
            startActivity(intent)
//            finish() // Close Welcome page
//            initiatePayment()
//            launchPhonePe()
            // Call function to initiate PhonePe transaction
//            initiatePhonePeTransaction(this, "your_salt", "your_salt_index", "receiver_account_id")
        }

        cardTrader.setOnClickListener {
            Toast.makeText(this, "Trader Card Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterSingup::class.java)
            val userRole = "Trader" // This value would be determined dynamically
            SharedPreferencesManager.saveUserRole(context = this, role = userRole)
            startActivity(intent)
        }

        cardShopkeeper.setOnClickListener {
            Toast.makeText(this, "Shopkeeper Card Clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, RegisterSingup::class.java)
            val userRole = "Shopkeeper" // This value would be determined dynamically
            SharedPreferencesManager.saveUserRole(context = this, role = userRole)
            startActivity(intent)
        }

        cardEmployement.setOnClickListener {
            Toast.makeText(this, "Employment Card Clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, RegisterSingup::class.java)
            val userRole = "Employee" // Employment = Employee
            SharedPreferencesManager.saveUserRole(context = this, role = userRole)
            startActivity(intent)
        }

        ratesCapsule.setOnClickListener {
            val intent = Intent(this, WelcomeRates:: class.java)
            val userRole = "Rates" // Employment = Employee
            SharedPreferencesManager.saveUserRole(context = this, role = userRole)
            startActivity(intent)
        }
    }

    fun setupPaymentInputs() {
        try {
            PhonePe.init(this, PhonePeEnvironment.SANDBOX, "YOUR_MERCHANT_ID", "YOUR_APP_ID")
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101) {
//            // Handle the payment result here
//            if (resultCode == RESULT_OK) {
//                // Payment was successful
//            } else {
//                // Payment failed
//            }
//        }
//    }


    private fun initiatePayment() {
        val deviceContext = JSONObject().apply {
            put("phonePeVersionCode", 303391)
        }
        // Create payment request
        val data = JSONObject().apply {
            put("merchantTransactionId", "M2306160483220675579140")
            put("merchantId", "e3e1mmcccdmm9ef8vdfmd7b")
            put("merchantUser Id", "unknown8898")
            put("amount", 100) // Amount in paise
            put("merchantOrderId", "OD139924923")
            put("mobileNumber", "7382612210") // User's mobile number
            put("message", "Payment towards order No.123456") // User's mobile number
            put("email", "saichand.iosdev@gmail.com") // User's mobile number
            put("shortName", "saichand") // User's mobile number
            put("paymentScope", "PHONEPE") // User's mobile number
            put("callbackUrl", "https://your.callback.url") // Your callback URL
            put("deviceContext", deviceContext)
        }

        // Build the payment request
        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(data.toString())
            .setChecksum("YOUR_CHECKSUM") // Generate checksum as per PhonePe's documentation
            .setUrl("/pg/v1/pay")
            .build()

        // Start the payment activity
        val intent = PhonePe.getImplicitIntent(this, b2BPGRequest, "com.phonepe.app")
        if (intent != null) {
            paymentLauncher.launch(intent)
        }
    }

    private fun launchPhonePe() {
        // Create an intent to launch PhonePe
        val deviceContext = JSONObject().apply {
            put("phonePeVersionCode", 303391)
        }

        val dataUri = Uri.parse(
            "phonepe://pay?" +
                    "callbackUrl=https://your.callback.url" +
                    "&amount=100" +
                    "&mobileNumber=7382612210" +
                    "&merchantId=e3e1mmcccdmm9ef8vdfmd7b" +
                    "&transactionId=M2306160483220675579140" +
                    "&merchantOrderId=OD139924923" +
                    "&message=Payment+towards+order+No.123456" +
                    "&email=saichand.iosdev@gmail.com" +
                    "&shortName=saichand" +
                    "&paymentScope=PHONEPE" +
                    "&deviceContext=" + Uri.encode(deviceContext.toString())
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = dataUri
        }

        // Check if PhonePe is installed
        if (intent.resolveActivity(packageManager) != null) {
            Toast.makeText(this, intent.toString(), Toast.LENGTH_SHORT).show()
            startActivity(intent)
        } else {
            Toast.makeText(this, "PhonePe app is not installed", Toast.LENGTH_SHORT).show()
        }
    }


//    private fun initiatePhonePePayment(
//        payeeVpa: String,
//        payeeName: String,
//        amount: String,
//        transactionId: String,
//        transactionRefId: String,
//        currency: String,
//        transactionNote: String
//    ) {
//        // Construct the UPI URI
//        val uri = Uri.parse(
//            "upi://pay?pa=$payeeVpa&pn=$payeeName&tid=$transactionId&tr=$transactionRefId&tn=$transactionNote&am=$amount&cu=$currency"
//        )
//
//        // Create an Intent
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            data = uri
//            setPackage("com.phonepe.app") // Ensures intent goes only to PhonePe
//        }
//
//        // Verify that PhonePe is installed and handle the payment intent
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivityForResult(intent, 101) // Use a request code to track result
//        } else {
//            Toast.makeText(this, "PhonePe app not installed", Toast.LENGTH_SHORT).show()
//            getInstalledUPIApps()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 101) {
//            when (resultCode) {
//                RESULT_OK -> {
//                    val response = data?.getStringExtra("response")
//                    Toast.makeText(this, "Payment Successful: $response", Toast.LENGTH_LONG).show()
//                }
//                RESULT_CANCELED -> {
//                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_LONG).show()
//                }
//                else -> {
//                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

//    private fun getInstalledUPIApps(): ArrayList<String> {
//        val upiList = ArrayList<String>()
//        val uri = Uri.parse("upi://pay")
//        val upiUriIntent = Intent().apply { data = uri }
//        val packageManager = packageManager
//        val resolveInfoList = packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
//        resolveInfoList?.forEach { resolveInfo ->
//            upiList.add(resolveInfo.activityInfo.packageName)
//        }
//        return upiList
//    }


    // SHA-256 Checksum Calculation
    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    // Function to initiate PhonePe Transaction
    private fun initiatePhonePeTransaction(context: Activity, salt: String, saltIndex: String, receiverAccountId: String) {
        val apiEndPoint = "/v3/debit"

        // Prepare Transaction Data
        val data = hashMapOf<String, Any>(
            "merchantId" to "M2306160483220675579140", // Replace with your Merchant ID
            "transactionId" to "TX123456789", // Transaction ID. Ensure uniqueness and less than 38 characters
            "amount" to 100L, // Transaction Amount in paise (e.g., 100 = ₹1.00)
            "merchantOrderId" to "OD1234", // Merchant Order ID. Ensure uniqueness and less than 48 characters
            "message" to "Test transaction", // Optional transaction message
            "mobileNumber" to "7382612210", // Customer's mobile number (Optional)
            "email" to "testuser@example.com", // Customer's email (Optional)
            "shortName" to "TestUser", // Optional short name
            "receiverAccountId" to receiverAccountId // Account ID where funds are credited (Mandatory for linking)
        )

        // Convert Data to JSON and Base64 Encode
        val gson = Gson()
        val base64Body =
            Base64.encodeToString(gson.toJson(data).toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

        // Generate Checksum
        val checksum = sha256("$base64Body$apiEndPoint$salt") + "###" + saltIndex

        // Build Transaction Request
        val debitRequest = TransactionRequestBuilder()
            .setData(base64Body)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()

        try {
            // Start PhonePe Transaction
            val intent = PhonePe.getTransactionIntent(debitRequest)
            if (intent != null) {
                startActivityForResult(intent, DEBIT_REQUEST_CODE)
            }
        } catch (e: PhonePeInitException) {
            e.printStackTrace()
        }
    }


    // Handling the Response After the Transaction
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEBIT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Retrieve transaction response from intent
                    val response = data?.getStringExtra(TRANSACTION_RESPONSE)
                    println("Transaction Successful: $response")
                }
                Activity.RESULT_CANCELED -> {
                    // Handle transaction cancellation
                    println("Transaction Cancelled")
                }
                else -> {
                    // Retrieve error response from intent
                    val errorResponse = data?.getStringExtra(ERROR_RESPONSE)
                    println("Transaction Failed: $errorResponse")
                }
            }
        }
    }

}
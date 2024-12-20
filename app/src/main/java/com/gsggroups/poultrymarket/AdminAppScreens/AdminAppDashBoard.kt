package com.gsggroups.poultrymarket.AdminAppScreens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gsggroups.poultrymarket.R
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class AdminAppDashBoard : AppCompatActivity() {
    private lateinit var btnSelectFile: Button
    private lateinit var btnProcessFile: Button
    private lateinit var tvFilePath: TextView
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_app_dash_board)


        btnSelectFile = findViewById<Button>(R.id.btnSelectFile)
        btnProcessFile = findViewById<Button>(R.id.btnProcessFile)
        tvFilePath = findViewById<TextView>(R.id.tvFilePath)

        btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            startActivityForResult(intent, 100)
        }

        btnProcessFile.setOnClickListener {
            selectedFileUri?.let { uri ->
                processExcelFile(uri)
            } ?: run {
                tvFilePath.text = "Please select a file first."
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let { uri ->
                val fileName = getFileName(uri)
                tvFilePath.text = "Selected File: $fileName"
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "p3o3l3"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    private fun processExcelFile(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val jsonArray = JSONArray()

            // Loop through the rows, starting from row 1 (skip header row)
            for (row in sheet) {
                if (row.rowNum == 0) continue // Skip header row

                val districtCell = row.getCell(0) // Column 0: District
                val liftingCell = row.getCell(1)  // Column 1: Lifting
                val retailCell = row.getCell(2)   // Column 2: Retail
                val skinCell = row.getCell(3)     // Column 3: Skin
                val skinlessCell = row.getCell(4) // Column 4: Skinless

                val district = districtCell?.toString() ?: "Unknown"
                val lifting = liftingCell?.toString() ?: "0"
                val retail = retailCell?.toString() ?: "0"
                val skin = skinCell?.toString() ?: "0"
                val skinless = skinlessCell?.toString() ?: "0"

                // Create a JSONObject for each row
                val jsonObject = JSONObject()
                jsonObject.put("district", district)
                jsonObject.put("lifting", lifting)
                jsonObject.put("retail", retail)
                jsonObject.put("skin", skin)
                jsonObject.put("skinless", skinless)

                jsonArray.put(jsonObject)
            }

            // Log the JSON array (or use it as needed)
            Log.d("ExcelData", jsonArray.toString())
            tvFilePath.text = "JSON created. Check logs for details."
        } catch (e: Exception) {
            e.printStackTrace()
            tvFilePath.text = "Error processing file."
        }
    }

}
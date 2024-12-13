package com.gsggroups.poultrymarket.Employement

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gsggroups.poultrymarket.R
import com.gsggroups.poultrymarket.databinding.ActivityEmployementDashboardBinding

class EmployementDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityEmployementDashboardBinding
    private lateinit var adapter: EmployerPageAdapter
    private lateinit var need_job_switch: Switch
    private lateinit var need_job_textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployementDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AllEmployersFragment())
                .commit()
        }

        need_job_switch = findViewById(R.id.need_job_switch1)
        need_job_textView = findViewById(R.id.need_job_switch1_text)

        // Set up a listener to change text color based on switch state
        need_job_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                need_job_textView.setTextColor(Color.parseColor("#006400"))
                need_job_textView.setTypeface(null, Typeface.BOLD)
            } else {
                need_job_textView.setTextColor(Color.RED)
                need_job_textView.setTypeface(null, Typeface.NORMAL)
            }
        }



    }
}
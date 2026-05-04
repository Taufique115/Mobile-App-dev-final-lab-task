package com.university.usersettings

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Profile Setup"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSpinners()

        binding.btnSaveProfile.setOnClickListener { saveProfile() }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupSpinners() {
        val deptAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.departments,
            android.R.layout.simple_spinner_item
        )
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDept.adapter = deptAdapter

        val yearAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.years,
            android.R.layout.simple_spinner_item
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter
    }

    private fun loadProfile() {
        val prefs = getSharedPreferences(Constants.PREFS_PROFILE, MODE_PRIVATE)
        val appPrefs = getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE)

        var name = prefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        if (name.isEmpty()) {
            name = appPrefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        }
        val id = prefs.getString(Constants.KEY_STUDENT_ID, "") ?: ""
        val dept = prefs.getString(Constants.KEY_DEPARTMENT, "") ?: ""
        val year = prefs.getString(Constants.KEY_YEAR, "") ?: ""
        val email = prefs.getString(Constants.KEY_EMAIL, "") ?: ""

        binding.etFullName.setText(name)
        binding.etStudentId.setText(id)
        binding.etEmail.setText(email)

        val depts = resources.getStringArray(R.array.departments)
        val deptIdx = depts.indexOf(dept)
        if (deptIdx >= 0) binding.spinnerDept.setSelection(deptIdx)

        val years = resources.getStringArray(R.array.years)
        val yearIdx = years.indexOf(year)
        if (yearIdx >= 0) binding.spinnerYear.setSelection(yearIdx)

        binding.tvWelcome.text =
            if (name.isNotEmpty()) "Welcome back, $name!" else "Welcome back!"
    }

    private fun saveProfile() {
        val name = binding.etFullName.text.toString().trim()
        val id = binding.etStudentId.text.toString().trim()
        val dept = binding.spinnerDept.selectedItem.toString()
        val year = binding.spinnerYear.selectedItem.toString()
        val email = binding.etEmail.text.toString().trim()

        getSharedPreferences(Constants.PREFS_PROFILE, MODE_PRIVATE).edit().apply {
            putString(Constants.KEY_STUDENT_NAME, name)
            putString(Constants.KEY_STUDENT_ID, id)
            putString(Constants.KEY_DEPARTMENT, dept)
            putString(Constants.KEY_YEAR, year)
            putString(Constants.KEY_EMAIL, email)
            apply()
        }

        getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE).edit()
            .putString(Constants.KEY_STUDENT_NAME, name)
            .apply()

        binding.tvWelcome.text =
            if (name.isNotEmpty()) "Welcome back, $name!" else "Welcome back!"
        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
    }
}

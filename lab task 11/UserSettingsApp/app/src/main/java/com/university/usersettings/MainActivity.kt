package com.university.usersettings

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Student Portal Settings"

        setupSpinner()
        setupSeekBar()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        restoreSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLang.adapter = adapter
    }

    private fun setupSeekBar() {
        binding.seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvFontValue.text = "Font Size: ${progress + 12}sp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener { saveSettings() }
        binding.btnReset.setOnClickListener { resetPreferences() }
        binding.btnViewSettings.setOnClickListener {
            startActivity(Intent(this, SettingsViewerActivity::class.java))
        }
    }

    private fun saveSettings() {
        val selectedTheme = when (binding.rgTheme.checkedRadioButtonId) {
            R.id.rbDark -> "dark"
            R.id.rbSystem -> "system"
            else -> "light"
        }

        val name = binding.etStudentName.text.toString().trim()
        val previousTheme = getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE)
            .getString(Constants.KEY_THEME, "light") ?: "light"

        getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE).edit().apply {
            putString(Constants.KEY_THEME, selectedTheme)
            putBoolean(Constants.KEY_NOTIFICATIONS, binding.switchNotif.isChecked)
            putString(Constants.KEY_LANGUAGE, binding.spinnerLang.selectedItem.toString())
            putInt(Constants.KEY_FONT_SIZE, binding.seekBarFont.progress + 12)
            putLong(Constants.KEY_LAST_SAVED, System.currentTimeMillis())
            putString(Constants.KEY_STUDENT_NAME, name)
            apply()
        }

        if (name.isNotEmpty()) {
            getSharedPreferences(Constants.PREFS_PROFILE, MODE_PRIVATE).edit()
                .putString(Constants.KEY_STUDENT_NAME, name)
                .apply()
        }

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()

        ThemeManager.applyFromPrefs(this)
        if (selectedTheme != previousTheme) {
            recreate()
        }
    }

    private fun resetPreferences() {
        val appPrefs = getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE)
        val previousTheme = appPrefs.getString(Constants.KEY_THEME, "light") ?: "light"
        appPrefs.edit().clear().apply()

        binding.rbLight.isChecked = true
        binding.switchNotif.isChecked = true
        binding.seekBarFont.progress = 4
        binding.spinnerLang.setSelection(0)
        binding.etStudentName.setText("")
        binding.tvFontValue.text = "Font Size: 16sp"

        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
        ThemeManager.applyFromPrefs(this)
        if (previousTheme != "light") {
            recreate()
        }
    }

    private fun restoreSettings() {
        val appPrefs = getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(Constants.PREFS_PROFILE, MODE_PRIVATE)

        when (appPrefs.getString(Constants.KEY_THEME, "light")) {
            "dark" -> binding.rbDark.isChecked = true
            "system" -> binding.rbSystem.isChecked = true
            else -> binding.rbLight.isChecked = true
        }

        binding.switchNotif.isChecked = appPrefs.getBoolean(Constants.KEY_NOTIFICATIONS, true)

        val fontSize = appPrefs.getInt(Constants.KEY_FONT_SIZE, 16)
        binding.seekBarFont.progress = fontSize - 12
        binding.tvFontValue.text = "Font Size: ${fontSize}sp"

        val lang = appPrefs.getString(Constants.KEY_LANGUAGE, "English") ?: "English"
        val langArray = resources.getStringArray(R.array.languages)
        val langIndex = langArray.indexOf(lang)
        if (langIndex >= 0) binding.spinnerLang.setSelection(langIndex)

        val nameFromApp = appPrefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        val nameFromProfile = profilePrefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        binding.etStudentName.setText(
            if (nameFromApp.isNotEmpty()) nameFromApp else nameFromProfile
        )
    }
}

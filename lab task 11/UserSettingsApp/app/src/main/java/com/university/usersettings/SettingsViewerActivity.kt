package com.university.usersettings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivitySettingsViewerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Saved Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loadSettings()
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadSettings() {
        val appPrefs = getSharedPreferences(Constants.PREFS_APP, MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(Constants.PREFS_PROFILE, MODE_PRIVATE)

        val lastSaved = appPrefs.getLong(Constants.KEY_LAST_SAVED, 0L)
        val hasAppSettings = lastSaved != 0L

        val nameApp = appPrefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        val nameProfile = profilePrefs.getString(Constants.KEY_STUDENT_NAME, "") ?: ""
        val studentId = profilePrefs.getString(Constants.KEY_STUDENT_ID, "") ?: ""
        val email = profilePrefs.getString(Constants.KEY_EMAIL, "") ?: ""

        val resolvedName = when {
            nameApp.isNotEmpty() -> nameApp
            nameProfile.isNotEmpty() -> nameProfile
            else -> ""
        }
        val studentInfoLines = buildList {
            if (resolvedName.isNotEmpty()) add(resolvedName)
            if (studentId.isNotEmpty()) add("ID: $studentId")
            if (email.isNotEmpty()) add(email)
        }
        val hasStudentInfo = studentInfoLines.isNotEmpty()

        if (!hasAppSettings && !hasStudentInfo) {
            binding.tvNoSettings.visibility = View.VISIBLE
            binding.tvNoSettings.text =
                getString(R.string.msg_no_settings_or_profile)
            binding.cardName.visibility = View.GONE
            binding.cardTheme.visibility = View.GONE
            binding.cardNotif.visibility = View.GONE
            binding.cardLang.visibility = View.GONE
            binding.cardFont.visibility = View.GONE
            binding.cardTimestamp.visibility = View.GONE
            binding.btnEdit.visibility = View.GONE
            return
        }

        binding.cardName.visibility = View.VISIBLE
        binding.btnEdit.visibility = View.VISIBLE

        if (!hasAppSettings && hasStudentInfo) {
            binding.tvNoSettings.visibility = View.VISIBLE
            binding.tvNoSettings.text = getString(R.string.msg_profile_only_no_portal_save)
        } else {
            binding.tvNoSettings.visibility = View.GONE
        }

        binding.cardTheme.visibility = if (hasAppSettings) View.VISIBLE else View.GONE
        binding.cardNotif.visibility = if (hasAppSettings) View.VISIBLE else View.GONE
        binding.cardLang.visibility = if (hasAppSettings) View.VISIBLE else View.GONE
        binding.cardFont.visibility = if (hasAppSettings) View.VISIBLE else View.GONE
        binding.cardTimestamp.visibility = if (hasAppSettings) View.VISIBLE else View.GONE

        binding.tvNameValue.text =
            if (studentInfoLines.isNotEmpty()) studentInfoLines.joinToString("\n") else "—"

        val theme = appPrefs.getString(Constants.KEY_THEME, "—") ?: "—"
        val notif = appPrefs.getBoolean(Constants.KEY_NOTIFICATIONS, true)
        val lang = appPrefs.getString(Constants.KEY_LANGUAGE, "—") ?: "—"
        val fontSize = appPrefs.getInt(Constants.KEY_FONT_SIZE, 16)

        binding.tvThemeValue.text = theme.replaceFirstChar { it.uppercase() }
        binding.tvNotifValue.text = if (notif) "Enabled" else "Disabled"
        binding.tvLangValue.text = lang
        binding.tvFontValue.text = "${fontSize}sp"

        if (hasAppSettings) {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            binding.tvTimestampValue.text = sdf.format(Date(lastSaved))
        }
    }
}

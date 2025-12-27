package com.example.LumiNote

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etIdNama: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnLogin: Button
    private lateinit var tvLupaPassword: TextView
    private lateinit var tvDaftar: TextView

    private lateinit var userManager: UserManager
    private lateinit var sessionManager: SessionManager
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        userManager = UserManager(this)
        sessionManager = SessionManager(this)
        setupListeners()
    }

    private fun initViews() {
        etIdNama = findViewById(R.id.etIdNama)
        etPassword = findViewById(R.id.etPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvLupaPassword = findViewById(R.id.tvLupaPassword)
        tvDaftar = findViewById(R.id.tvDaftar)
    }

    private fun setupListeners() {
        // Toggle password visibility
        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Login button
        btnLogin.setOnClickListener {
            login()
        }

        // Lupa password
        tvLupaPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Daftar
        tvDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off)
        } else {
            // Show password
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            btnTogglePassword.setImageResource(R.drawable.ic_eye_on)
        }
        isPasswordVisible = !isPasswordVisible
        etPassword.setSelection(etPassword.text.length)
    }

    private fun login() {
        val idNama = etIdNama.text.toString().trim()
        val password = etPassword.text.toString()

        // Validasi input
        if (idNama.isEmpty()) {
            Toast.makeText(this, "ID Nama tidak boleh kosong 🤪", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong 🤪", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek login
        val user = userManager.loginUser(idNama, password)

        if (user != null) {
            // Login berhasil
            sessionManager.createLoginSession(idNama)
            Toast.makeText(this, "Login berhasil! Selamat datang ${user.nama} ✨✨", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // Login gagal
            Toast.makeText(this, "ID Nama atau Password salah 🥺", Toast.LENGTH_SHORT).show()
        }
    }
}
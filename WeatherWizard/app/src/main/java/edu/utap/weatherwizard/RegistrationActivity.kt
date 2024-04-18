package edu.utap.weatherwizard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import edu.utap.weatherwizard.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import edu.utap.weatherwizard.databinding.ActivityRegistrationBinding


class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.guestButton.setOnClickListener {
            val intent = Intent(
                this@RegistrationActivity,
                MainActivity::class.java
            )
            startActivity(intent)
        }
        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(
                this@RegistrationActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            if (email.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Email is empty!",
                    Toast.LENGTH_LONG
                ).show()
            } else if (password.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Password is empty!",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                binding.progressbar.visibility = View.VISIBLE
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Signup successful!",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            this@RegistrationActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            applicationContext, "Registration failed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    binding.progressbar.visibility = View.INVISIBLE

                }
            }
        }
    }
}
package edu.utap.weatherwizard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.utap.weatherwizard.MainActivity
import edu.utap.weatherwizard.databinding.ActivityLoginBinding
import edu.utap.weatherwizard.databinding.ActivityRegistrationBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpRedirectText.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                RegistrationActivity::class.java
            )

            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
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
            }
            else{
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )

                    intent.putExtra("username", it.user?.displayName)
                    intent.putExtra("useremail", it.user?.email)
                    intent.putExtra("userid", it.user?.uid)


                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Login failed. Check your credentials.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}
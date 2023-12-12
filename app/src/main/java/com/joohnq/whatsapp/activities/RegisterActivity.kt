package com.joohnq.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.databinding.ActivityRegisterBinding
import com.joohnq.whatsapp.util.showMessage
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val context = this
    private val auth by lazy { UserAuth() }
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        initEventsClick()
    }

    private fun initEventsClick() {
        with(binding) {
            btnRegister.setOnClickListener {
                initFields()
                val itsOk: Boolean = validateField()
                if (itsOk) {
                    lifecycleScope.launch {
                        auth.registerUser(
                            name,
                            email,
                            password,
                            onSuccess = { message ->
                                showMessage(message)
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            },
                            onFailure = { message, exception ->
                                showMessage(message)
                                try {
                                    throw exception
                                } catch (invalidCredentialsException: FirebaseAuthInvalidCredentialsException) {
                                    tilRegisterEmail.error = "Email inválido!"
                                } catch (userCollisionException: FirebaseAuthUserCollisionException) {
                                    tilRegisterEmail.error = "Esse email já foi usado!"
                                } catch (weakPasswordException: FirebaseAuthWeakPasswordException) {
                                    tilRegisterPassword.error = "Senha fraca!"
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun initFields() {
        with(binding) {
            name = tietRegisterName.text.toString()
            email = tietRegisterEmail.text.toString()
            password = tietRegisterPassword.text.toString()
        }
    }

    private fun validateField(): Boolean {
        var isValid = true

        with(binding) {
            if (name.isNotEmpty()) {
                tilRegisterName.error = null
            } else {
                tilRegisterName.error = "Por favor preencha com seu nome"
                isValid = false
            }
            if (email.isNotEmpty()) {
                tilRegisterEmail.error = null
            } else {
                tilRegisterEmail.error = "Por favor preencha com seu email"
                isValid = false
            }
            if (password.isNotEmpty()) {
                tilRegisterPassword.error = null
            } else {
                tilRegisterPassword.error = "Por favor preencha com sua senha"
                isValid = false
            }
        }
        return isValid
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbar.mtMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
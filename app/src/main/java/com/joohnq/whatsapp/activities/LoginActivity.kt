package com.joohnq.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.databinding.ActivityLoginBinding
import com.joohnq.whatsapp.util.showMessage
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth by lazy { UserAuth() }
    private val context = this
    private lateinit var email: String
    private lateinit var password: String

    override fun onStart() {
        super.onStart()
        if(auth.hasUserLogged()){
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initEventsClick()
//        auth.logoutUser()
    }

    private fun initEventsClick() {
        with(binding){
            tvRegister.setOnClickListener {
                val intent = Intent(context, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                initFields()
                val itsOk: Boolean = validateField()
                if (itsOk) {
                    lifecycleScope.launch {
                        auth.loginUser(
                            email,
                            password,
                            onSuccess = {
                                showMessage("Sucesso ao logar usuário")
                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            },
                            onFailure = { exception ->
                                showMessage("Erro ao logar usuário")
                                try {
                                    throw exception
                                } catch (invalidCredentialsException: FirebaseAuthInvalidUserException) {
                                    tilLoginEmail.error = "Email não cadastrado!"
                                }catch (invalidCredentialsException: FirebaseAuthInvalidCredentialsException) {
                                    tilLoginEmail.error = "Email ou senha estão incorretos!"
                                    tilLoginPassword.error = "Email ou senha estão incorretos!"
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
            email = tietLoginEmail.text.toString()
            password = tietLoginPassword.text.toString()
        }
    }

    private fun validateField(): Boolean {
        var isValid = true

        with(binding) {
            if (email.isNotEmpty()) {
                tilLoginEmail.error = null
            } else {
                tilLoginEmail.error = "Por favor preencha com seu email"
                isValid = false
            }
            if (password.isNotEmpty()) {
                tilLoginPassword.error = null
            } else {
                tilLoginPassword.error = "Por favor preencha com sua senha"
                isValid = false
            }
        }
        return isValid
    }
}
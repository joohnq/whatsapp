package com.joohnq.whatsapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.joohnq.whatsapp.databinding.ActivityProfileBinding
import com.joohnq.whatsapp.db.UserDatabase
import com.joohnq.whatsapp.storage.UserStorage
import com.joohnq.whatsapp.util.showMessage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val context = this
    private val db by lazy { UserDatabase() }
    private val storage by lazy { UserStorage() }
    private var hasCameraPermission = false
    private var hasGaleryPermission = false
    private val picasso by lazy { Picasso.get() }
    private val galeryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.ivProfilePhoto.setImageURI(uri)
            saveImageOnStorageAndDatabase(uri)
        } else {
            showMessage("Nenhuma imagem selecionada")
        }
    }

    override fun onStart() {
        super.onStart()
        initUser()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        solicitPermission()
        initEventsClick()
    }

    private fun initEventsClick() {
        with(binding) {
            fabAddPhoto.setOnClickListener {
                if (hasGaleryPermission) {
                    galeryManager.launch("image/*")
                } else {
                    solicitPermission()
                }
            }
            btnSave.setOnClickListener {
                val name = binding.tietProfileName.text.toString()
                val itsOk = validateFields(name)
                if (itsOk) {
                    db.updateUserName(
                        name,
                        onSuccess = { message ->
                            showMessage(message)
                        },
                        onFailure = { message, exception ->
                            showMessage(message)
                            Log.i("database_info", exception.toString())
                        },
                    )
                }
            }
        }
    }

    private fun validateFields(name: String): Boolean {
        var isValid = true
        if (name.isNotEmpty()) {
            binding.tilProfileName.error = null
        } else {
            binding.tilProfileName.error = "Por favor preencha com seu nome"
            isValid = false
        }
        return isValid
    }

    private fun saveImageOnStorageAndDatabase(uri: Uri) {
        storage.updateProfilePhoto(
            uri,
            onSuccess = { message, url ->
                showMessage(message)
                db.saveUserProfilePhoto(
                    url,
                    onSuccess = { message ->
                        showMessage(message)
                    },
                    onFailure = { message, exception ->
                        showMessage(message)
                        Log.i("database_info", exception.toString())
                    },
                )
            },
            onFailure = { message, exception ->
                showMessage(message)
                Log.i("storage_info", exception.toString())
            }
        )
    }

    private fun solicitPermission() {
        val permissionsDeniedList = mutableListOf<String>()

        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasGaleryPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasGaleryPermission) {
            permissionsDeniedList.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (!hasCameraPermission) {
            permissionsDeniedList.add(Manifest.permission.CAMERA)
        }

        if (permissionsDeniedList.isNotEmpty()) {
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
                hasGaleryPermission =
                    permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasGaleryPermission
            }.launch(permissionsDeniedList.toTypedArray())
        }
    }

    private fun initUser() {
        db.getUser(
            onSuccess = { user ->
                val userName = user.name
                val userProfilePhoto = user.profilePhoto
                binding.tietProfileName.setText(userName)
                if (!userProfilePhoto.isNullOrEmpty()) {
                    picasso
                        .load(userProfilePhoto)
                        .into(binding.ivProfilePhoto)
                }else{

                }
            },
            onFailure = { exception ->
                Log.i("getUser_info", exception.toString())
            },
        )
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbarProfile.mtMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
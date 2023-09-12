package com.example.wegarb.presentation.view.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wegarb.databinding.ActivityLoggingBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoggingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggingBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var authorization: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebaseAuthentification()
        initLauncher()
        onClickSignInWithGoogle()
        checkNeedAuthorization()
    }

    private fun initFirebaseAuthentification(){
        authorization = Firebase.auth
    }

    private fun initLauncher() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultFromUserChoiceAccount ->
            val accountUserData = GoogleSignIn.getSignedInAccountFromIntent(resultFromUserChoiceAccount.data)
            getUserAccount(accountUserData)
        }
    }

    private fun getUserAccountGoogle(): GoogleSignInClient {
        val requestChoiceWindow = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .build()
        val googleWindowSignIn = GoogleSignIn.getClient(this, requestChoiceWindow)
        return googleWindowSignIn
    }

    private fun onClickSignInWithGoogle() {
        binding.buttonSignInWithGoogle.setOnClickListener {
            val userAccountGoogle = getUserAccountGoogle()
            launcher.launch(userAccountGoogle.signInIntent)
        }
    }

    private fun connectFirebaseAndUserAccount(userToken: String) {
        val userData = GoogleAuthProvider.getCredential(userToken, null)
        authorization.signInWithCredential(userData).addOnCompleteListener { processAuthorisation ->
            checkProcessAuthorization(processAuthorisation)
        }
    }

    private fun checkProcessAuthorization(processAuthorisation: Task<AuthResult>) {
        if(processAuthorisation.isSuccessful){
           checkNeedAuthorization()
        } else {
            Log.d("AuthFirebase","AuthFirebase error")
        }
    }
    private fun getUserAccount(accountUserData: Task<GoogleSignInAccount>) {
        try {
            val userAccount = accountUserData.getResult(ApiException::class.java)
            if(userAccount != null) {
                connectFirebaseAndUserAccount(userAccount.idToken!!)
            }
        } catch(e: ApiException) {
            Log.d("AuthFirebase","Api Connection error")
        }
    }

    private fun checkNeedAuthorization() {
        if(authorization.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
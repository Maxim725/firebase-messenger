package com.example.firebase_messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebase_messenger.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data);

            try {
                val account = task.getResult(ApiException::class.java)

                if(account !== null) {
                    firebaseAuthWithGoogle(account.idToken!!);
                }
            } catch (e: ApiException) {
                Log.d("[dev:sign-in-activity]", "API EXCEPTION")
            }
        }

        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        checkAuthState()
    }

    private fun getClient(): GoogleSignInClient {
        val gso =  GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(tokenId: String) {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                Log.d("[dev:sign-in-activity]", "sign in success")
                checkAuthState()
            } else {
                Log.d("[dev:sign-in-activity]", "sign in failed")
            }
        }
    }

    private fun checkAuthState() {
        Log.v("DEV_DEV_DEV", "auth: ${auth !== null} current user ${auth.currentUser !== null} email ${auth.currentUser?.email}")
        if(auth.currentUser !== null) {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }
}
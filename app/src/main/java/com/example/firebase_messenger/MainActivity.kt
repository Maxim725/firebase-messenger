package com.example.firebase_messenger

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_messenger.databinding.ActivityMainBinding
import com.google.android.gms.common.util.JsonUtils
import com.google.android.gms.tasks.Task
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        auth = Firebase.auth

        setContentView(binding.root)

        val database = Firebase.database
        val myRef = database.getReference("messages")

        binding.sendMessageButton.setOnClickListener {
            val text = binding.fieldOfTextMessage.text.toString()
            if(text.isNotEmpty()) {
                val displayName = auth.currentUser?.displayName
                var userName: String? = "[Unknown user]"

                if(displayName !== null && displayName.isNotEmpty()) {
                    userName = displayName
                }

//                val message = "$userName: $text";
//                val date: String = getFormattedDate()
//
//                val parsedFormattedDate = serializeFormattedDate(getFormattedDate())

//                Log.d("DEV_DEV_DEV", "$date: $parsedFormattedDate")
//                val msgRef = myRef.child("$parsedFormattedDate")

                myRef.child(myRef.push().key ?: "default").setValue(User(userName, text))
            }

            binding.fieldOfTextMessage.text.clear()
        }

        onChangeListener(myRef)
        setupActionBar()
        InitListOfMessagesView()
    }

    private fun InitListOfMessagesView() = with(binding) {
        adapter = UserAdapter()
        listOfMessages.layoutManager = LinearLayoutManager(this@MainActivity);
        listOfMessages.adapter = adapter;
    }

    private fun onChangeListener(dRef: DatabaseReference) {
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            finish()
        }

        dRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>();
                for(message in snapshot.children) {
                    val user = message.getValue(User::class.java)

                    if(user !== null) {
                        list.add(user);
                    }
                }

                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    private fun setupActionBar() {
        val ab = supportActionBar;

        val user = auth.currentUser

        Log.d("[DEV_DEV_DEV]", "Display name ${auth.currentUser?.displayName}")
        Log.d("[DEV_DEV_DEV]", "on setupActionBar ${Date().toString() + "s"}")
        binding.chatToolbar.title = auth.currentUser?.displayName

        if(user?.photoUrl !== null) {
            Thread {
                val bitMapImg = Picasso.get().load(user?.photoUrl).get()
                val drawableIcon = BitmapDrawable(resources, bitMapImg)

                runOnUiThread {
                    ab?.setDisplayHomeAsUpEnabled(true)
                    ab?.setHomeAsUpIndicator(drawableIcon)
                    ab?.title = auth.currentUser?.displayName


                    binding.avatar.setImageBitmap(bitMapImg);
                }
            }.start()
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d("[debug:main-activity]", "on start");
    }

    override fun onResume() {
        super.onResume();
        Log.d("[debug:main-activity]", "on resume");
    }

    override fun onPause() {
        super.onPause();
        Log.d("[debug:main-activity]", "on pause");
    }

    override fun onStop() {
        super.onStop();
        Log.d("[debug:main-activity]", "on stop");
    }

    override fun onDestroy() {
        super.onDestroy();
        Log.d("[debug:main-activity]", "on destroy");
    }

    override fun onRestart() {
        super.onRestart();
        Log.d("[debug:main-activity]", "on restart");
    }

    private fun getFormattedDate(): String {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, TimeFormat.CLOCK_24H, Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    private fun serializeFormattedDate(date: String): String {
        val charList = mutableListOf<Char>()

        for(char in date.toCharArray()) {
            when (char) {
                '.' -> {
                    charList.add('-')
                }
                ' ' -> {
                    charList.add('-')
                }
                ':' -> {
                    charList.add('-')
                }
                '+' -> {
                    charList.add('-')
                }
                else -> {
                    charList.add(char)
                }
            }
        }

        return charList.joinToString("") { "${it.toString()}"}
    }

    private fun deserializeFormattedDate(date: String): String {
        return ""
    }
}
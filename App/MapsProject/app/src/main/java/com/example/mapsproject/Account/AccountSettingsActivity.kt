package com.example.mapsproject.Account

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources

import android.graphics.BitmapFactory

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.widget.*
import com.example.mapsproject.Account.Account.getUserEmail
import com.example.mapsproject.Account.Account.getUserName
import com.example.mapsproject.Account.Account.updateUserEmail
import com.example.mapsproject.Account.Account.updateUserName
import com.example.mapsproject.Account.Account.user
import com.example.mapsproject.Configuration.MultiPlayerServerConf
import com.example.mapsproject.MainActivity
import com.example.mapsproject.R
import com.example.mapsproject.StartGameActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*


class AccountSettingsActivity: Activity() {


    val storageRef = Account.storage.reference
    val path : String = "images/"+ Account.getUserID()
    val  imagesRef: StorageReference? = storageRef.child(path)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set language config
        val myLocale = Locale(MultiPlayerServerConf.language)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)

        setContentView(R.layout.activity_account_settings)
        findViewById<TextView>(R.id.account_name).text= getUserName()
        findViewById<TextView>(R.id.account_email).text= getUserEmail()

        findViewById<Button>(R.id.edit_account_name_btn).setOnClickListener { view ->
            var newNameEntry = findViewById<EditText>(R.id.edit_account_name).text
            if (newNameEntry == null) {
                Toast.makeText(this, "Enter a new user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newName = newNameEntry.toString()
            if (newName == "") {
                Toast.makeText(this, "Enter a new user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateUserName(newName, this)

            findViewById<TextView>(R.id.account_name).text= getUserName()
            findViewById<EditText>(R.id.edit_account_name).setText("")


        }

        findViewById<Button>(R.id.edit_account_email_btn).setOnClickListener { view ->
            var newEmailEntry = findViewById<EditText>(R.id.edit_account_email).text
            if (newEmailEntry == null) {
                Toast.makeText(this, "Enter a new user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newEmail = newEmailEntry.toString()
            if (newEmail == "") {
                Toast.makeText(this, "Enter a new user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateUserEmail(newEmail, this)

            findViewById<TextView>(R.id.account_email).text= getUserEmail()
            findViewById<EditText>(R.id.edit_account_email).setText("")


        }
        //listener on turn back button
        this.findViewById<FloatingActionButton>(R.id.turn_back_profile).setOnClickListener {
            val i = Intent(this, StartGameActivity::class.java)
            finish()
            startActivity(i)
        }

        val imageView = findViewById<ImageView>(R.id.avatar)
        val localFile = File.createTempFile("images", "jpg")
        imagesRef?.getFile(localFile)?.addOnSuccessListener {
            // Local temp file has been created
            imageView.setImageBitmap((BitmapFactory.decodeFile(localFile.path)))
        }?.addOnFailureListener {
            // Handle any errors
            null
        }

        

        findViewById<Button>(R.id.new_pic_btn).setOnClickListener { view->
            val i = Intent(this, UpdatePicActivity::class.java)
            startActivity(i)
        }
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        if(user == null){
            Toast.makeText(this, "Error in accout user logIn", Toast.LENGTH_SHORT)
            Handler(Looper.getMainLooper()).postDelayed({ restart() }, 1000L)
            finish()
        }
        /*
        val storageRef = Firebase.storage.reference

        val imageView = findViewById<ImageView>(R.id.avatar)
        Glide.with(this).load(storageRef).into(imageView)*/
    }


    private fun restart() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
    // [END on_start_check_user]

    override fun onBackPressed() {
        val i = Intent(this, StartGameActivity::class.java)
        finish()
        startActivity(i)
    }

}



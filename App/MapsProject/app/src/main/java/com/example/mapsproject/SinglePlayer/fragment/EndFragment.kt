package com.example.mapsproject.SinglePlayer.fragment

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.mapsproject.Account.Account
import com.example.mapsproject.Account.Account.getHighScore
import com.example.mapsproject.Account.Account.uploadUserToFireStore
import com.example.mapsproject.Configuration.SinglePlayerServerConf
import com.example.mapsproject.Configuration.SinglePlayerServerConf.Companion.score
import com.example.mapsproject.MainActivity
import com.example.mapsproject.R
import com.example.mapsproject.StartGameActivity

class EndFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(context)
                        .setTitle(getString(R.string.title_back_press))
                        .setMessage(R.string.msg_back_press)
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            val i = Intent(activity, StartGameActivity::class.java)
                            // finish()
                            startActivity(i)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val mysong = MediaPlayer.create(context,R.raw.effetto_vincita)
        if(SinglePlayerServerConf.soundOn){mysong.start()}
        val rootView = inflater.inflate(R.layout.fragment_end_singleplayer, container, false)
        rootView.findViewById<Button>(R.id.end_btn).setOnClickListener{view->
            if(SinglePlayerServerConf.soundOn){mysong.pause()}
            val i =  Intent(activity, StartGameActivity::class.java)
            startActivity(i)
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPref?.edit()
        var highscore = getHighScore()
        if(score!! > highscore!!){
            Account.db.collection("users").document(Account.getUserID()).update("highscore", score)

        }

        rootView.findViewById<TextView>(R.id.high_score_end).setText(highscore.toString())
        rootView.findViewById<TextView>(R.id.score_end).setText(score.toString())

        return rootView
    }
}
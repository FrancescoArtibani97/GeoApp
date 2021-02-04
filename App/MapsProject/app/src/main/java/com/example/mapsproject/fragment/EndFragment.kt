package com.example.mapsproject.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mapsproject.MainActivity
import com.example.mapsproject.R

class EndFragment: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_end_singleplayer, container, false)
        rootView.findViewById<Button>(R.id.end_btn).setOnClickListener{view->
            val i =  Intent(activity, MainActivity::class.java)
            startActivity(i)
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPref?.edit()
        var score = sharedPref?.getInt(getString(R.string.current_score_key), 0)
        var highscore = sharedPref?.getInt(getString(R.string.high_score_key), 0)

        rootView.findViewById<TextView>(R.id.high_score_end).setText(highscore.toString())
        rootView.findViewById<TextView>(R.id.score_end).setText(score.toString())

        return rootView
    }
}
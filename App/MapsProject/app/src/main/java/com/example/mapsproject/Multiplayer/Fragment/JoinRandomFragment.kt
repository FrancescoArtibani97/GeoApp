package com.example.mapsproject.Multiplayer.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mapsproject.Configuration.MultiPlayerServerConf
import com.example.mapsproject.Configuration.MultiPlayerServerConf.Companion.player_id
import com.example.mapsproject.R
import org.json.JSONObject

class JoinRandomFragment:Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MultiPlayerServerConf.queue = Volley.newRequestQueue(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_loading_view, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object : Thread() {
            override fun run(){
                super.run()
                Handler(Looper.getMainLooper()).postDelayed({joinRandomGame()}, MultiPlayerServerConf.pollingPeriod)
            }
        }.start()
    }

    private fun joinRandomGame() {
        MultiPlayerServerConf.randomVar=1
        Log.i("myTag","request: "+ MultiPlayerServerConf.url +"req="+ MultiPlayerServerConf.startGameReq+"&random=1")
        val stringRequest = StringRequest(
                Request.Method.GET,  MultiPlayerServerConf.url +"req="+ MultiPlayerServerConf.startGameReq+"&random=1",{
            response->
            val reply = JSONObject(response.toString())
            MultiPlayerServerConf.game_id = reply!!.getInt("game_id")
            MultiPlayerServerConf.player_id = reply!!.getInt("player_id")



            Log.i("myTag","game id: "+ MultiPlayerServerConf.game_id+"; player id: "+ MultiPlayerServerConf.player_id)

            if(player_id==0){
                findNavController().navigate(R.id.action_joinRandomFragment_to_poolingNewGameFragment)
            }
            else {
                findNavController().navigate(R.id.action_joinRandomFragment_to_waitingJoinFragment)
            }
        },{ error: VolleyError? ->
            Log.i("info", "Polling: " + error.toString())
            Toast.makeText(activity,"Error:" + error.toString(), Toast.LENGTH_SHORT)
            findNavController().navigate(R.id.action_joinRandomFragment_to_startFragmentMP)
        })
        MultiPlayerServerConf.queue?.add(stringRequest)

    }
}
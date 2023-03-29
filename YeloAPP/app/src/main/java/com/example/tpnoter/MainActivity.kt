package com.example.tpnoter

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val ListStation : ArrayList<Station> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Appel de la méthode pour lire l'api
        chargementDonnees()

        // gestion du click sur le boutton pour l'affichage de toutes les stations
        val button : Button = findViewById<Button>(R.id.button)
        button.setOnClickListener() {

            // lancement de la fenêtre avec plusieurs stations comme argument
            val intent: Intent =
                Intent(this@MainActivity, MapsActivity::class.java).apply {
                    putExtra("listeStation", ListStation)
                }
            startActivity(intent)
        }

        // gestion du click sur un item du ListView pour l'affichage d'une station
        val listViewStation: ListView = findViewById<ListView>(R.id.ListView1)
        listViewStation.setOnItemClickListener { parent, _, position, _ ->
            val station : Station = parent.getItemAtPosition(position) as Station

            // lancement de la fenêtre avec une seul station comme argument
            val intent: Intent =
                Intent(this@MainActivity, MapsActivity::class.java).apply {
                    putExtra("station",  station)
                }
            startActivity(intent)
        }
    }

    // Methode pour lire l'api
    fun chargementDonnees() {
        val client: OkHttpClient = OkHttpClient()
        val url : String =
            "https://api.agglo-larochelle.fr/production/opendata/api/records/1.0/search/dataset=yelo___disponibilite_des_velos_en_libre_service&facet=station_nom&api-key=a237e560-ca4b-4059-86c6-4f9111b6ae7a"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    // Parsage des données de l'api
                    val json : JSONObject = JSONObject(response.body!!.string())
                    val jsonRecord : JSONArray= json.getJSONArray("records")

                    // Récupération de toutes les données nécessaires
                    for (i in 0 until jsonRecord.length()) {
                        val nbEmplacement : Int= jsonRecord.getJSONObject(i).getJSONObject("fields")
                            .getInt("nombre_emplacements")
                        val nom : String = jsonRecord.getJSONObject(i).getJSONObject("fields")
                            .getString("station_nom").substring(3)
                        val accrocheLibre : Int = jsonRecord.getJSONObject(i).getJSONObject("fields")
                            .getInt("accroches_libres")
                        val lon : Double= jsonRecord.getJSONObject(i).getJSONObject("fields")
                            .getDouble("station_longitude")
                        val lat : Double = jsonRecord.getJSONObject(i).getJSONObject("fields")
                            .getDouble("station_latitude")

                        // Création de notre station et ajout dans la liste
                        ListStation.add(Station(nom, nbEmplacement, lon, lat, accrocheLibre))
                    }
                }

                // Lancement d'un Thread pour mettre à jour la vue principale avec la liste des stations
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    run() {

                        // Ajout de la liste dans notre ListView
                        val listViewStation: ListView = findViewById<ListView>(R.id.ListView1)
                        listViewStation.adapter = ArrayAdapter<Station>(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            ListStation
                        )
                    }
                })
            }
        })
    }
}
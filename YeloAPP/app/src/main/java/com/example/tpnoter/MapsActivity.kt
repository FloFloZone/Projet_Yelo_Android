package com.example.tpnoter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tpnoter.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (intent.getParcelableExtra<Station>("station") != null) {

            // Récupération de la station
            val station: Station? = intent.getParcelableExtra<Station>("station")

            // Affichage du point et de son point d'arrêt
            if (station != null) {
                findViewById<TextView>(R.id.informationStation).setText(station.nomStation)
                val point : LatLng = LatLng(station.latitude, station.longitude)
                mMap.addMarker(
                    MarkerOptions().position(point).title(station.nomStation).snippet(
                        "${station.placeLibre} / ${station.nbEmplacement} places"
                    )
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13f))
            }
        } else {

            // Reccupération de la liste
            var arrayListStation: ArrayList<Station> = arrayListOf<Station>()
            arrayListStation = intent.getParcelableArrayListExtra<Station>("listeStation")!!

            // Calcul du point moyen de l'ensemble des points
            val latitudeMoyenne: Double = arrayListStation.map { it.latitude }.average()
            val longitudeMoyenne: Double = arrayListStation.map { it.longitude }.average()

            // Affichage de tous les points
            for (i in 0 until arrayListStation.size) {
                val point =
                    LatLng(arrayListStation.get(i).latitude, arrayListStation.get(i).longitude)
                mMap.addMarker(
                    MarkerOptions().position(point).title(arrayListStation.get(i).nomStation)
                        .snippet(
                            "${arrayListStation.get(i).placeLibre} / ${arrayListStation.get(i).nbEmplacement} places"
                        )
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13f))
            }

            // zoom sur le point moyen de l'ensemble des points
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latitudeMoyenne,
                        longitudeMoyenne
                    ), 14f
                )
            )
        }
    }
}
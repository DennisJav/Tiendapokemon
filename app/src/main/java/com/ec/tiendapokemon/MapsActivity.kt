package com.ec.tiendapokemon

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ec.tiendapokemon.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val tiendas = mutableListOf<Tienda>()
    private val userLocation = Location("")
    private lateinit var myLocationButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        addTiendas()



        requestLocationPermission()

    }


    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                val permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(permissionArray, LOCATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            getUserLocation()
        }
    }

    //permisos para acceder a la ubicacion
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
             getUserLocation()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showLocationPermissionRationalDialog()
        } else {
            finish()
        }

    }

    private fun showLocationPermissionRationalDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Necesitas permiso de ubicacion")
            .setMessage("Acepta el permiso para buscar tiendas")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }.setNegativeButton("No") { _, _ ->
                finish()
            }
        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location? ->
            if(location != null){

                userLocation.latitude = location.latitude
                userLocation.longitude = location.longitude
                setupMap()
            }
        }
    }


    private fun setupMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    private fun addTiendas() {
        tiendas.add(Tienda("AnimeStore", -0.26715, -78.52215))
        tiendas.add(Tienda("AnimeStore", -0.25106, -78.52030))
        tiendas.add(Tienda("AnimeStore", -0.20333, -78.49940))
        tiendas.add(Tienda("AnimeStore", -0.20239, -78.49156))
        tiendas.add(Tienda("AnimeStore", -0.17631, -78.47851))
        tiendas.add(Tienda("AnimeStore", -0.16394, -78.48733))
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
        val icon = getTiendaIcon()
        for (tienda in tiendas) {
            val tiendaposition = LatLng(tienda.latitud, tienda.longitud)
            val tiendaName = tienda.name

            //distancia a las tiendas
            val tiendaLocation = Location("")
            tiendaLocation.latitude = tienda.latitud
            tiendaLocation.longitude = tienda.longitud

            val distance = tiendaLocation.distanceTo(userLocation)

            val marKerOption = MarkerOptions().position(tiendaposition).title(tiendaName)
                .snippet("Distance: $distance")
                .icon(icon)

            mMap.addMarker(marKerOption)
        }
        // Add a marker in Sydney and move the camera
        val userMarker = LatLng(userLocation.latitude, userLocation.longitude)
        mMap.addMarker(MarkerOptions().position(userMarker).title("User location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 13.0f))

        binding.myLocationBtn.findViewById<FloatingActionButton>(R.id.my_location_btn).setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 13.0f))
        }


    }

    private fun getTiendaIcon(): BitmapDescriptor {
        //el archivo a convertir debe ser deley svg
        val drawable = ContextCompat.getDrawable(this, R.drawable.store)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}
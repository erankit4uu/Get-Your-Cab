package com.example.ankit.ubercloneapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.example.ankit.ubercloneapplication.Utils.Utility
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DriverMapActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private lateinit var mMap: GoogleMap

    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mLocation: Location
    lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)


        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        buildGoogleApiClient()
        mMap.isMyLocationEnabled = true


    }

    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {
        mLocation = location!!
        val latLng = LatLng(mLocation.latitude, mLocation.longitude)

//        mMap.addMarker(MarkerOptions().position(latLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap.animateCamera(CameraUpdateFactory.zoomTo())

        val cameraPosition = CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val userID = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("DriverAvailable")
        val geoFire = GeoFire(ref)
        geoFire.setLocation(userID, GeoLocation(location.latitude, location.longitude))

    }


    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnected(p0: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = Utility.timeInterval
        mLocationRequest.fastestInterval = Utility.timeInterval
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onStop() {
        super.onStop()

        val userID = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("Drivers")
        val geoFire = GeoFire(ref)
        geoFire.removeLocation(userID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id = item?.itemId

        if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@DriverMapActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}

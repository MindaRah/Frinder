package com.example.frinder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

//Global variable
private val REQUEST_CODE = 100


private lateinit var slideAnimation: Animation
private lateinit var slideOutAnimation: Animation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Use default animation
        slideAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)


        //You can animate views here as well.
        open_settings_button.setOnClickListener {
            //Open new implicit intent
            //Set permissions to null
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                val uri = Uri.fromParts("package", packageName, null)
                //val uri = Uri.fromParts("package", packageName, "Permissions")
                //val uri = Uri.fromParts("package", packageName, "1")
                //package://com.example.frinder/permissions
                //package:com.example.frinder#permissions
                Log.d("Tag_H", uri.toString())
                it.data = uri
            })

        }
    }

    override fun onStart() {
        super.onStart()
        //Requesting Runtime permissions
        //1st Step: Check if permission is enabled
        //Check manifest if location permissions were granted
        if(ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            hideOverLay()
            //Permission is already granted - continue with application flow
            //Get location....
            //Create method - to keep on asking for the same permission
            //requestLocationPermission()
            //getDeviceLocation
            registerLocationListener()
        }
        else {
            //If not granted then request location permission. The system will show the permission dialog.
            // Whatever the user chooses the onRequestPermissionsResult will be called when you click a button otherwise this will not be called.
            //2nd Step; If permission is denied, request permissions
            requestLocationPermission()
        }
    }

    private fun registerLocationListener() {

    }

    //It can be an array because then you can listen to more than one permissions.
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Check if the request code is the same.
        if(requestCode == REQUEST_CODE && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //this is already granted
                //Get location....
                registerLocationListener()
            } else { //Permission not granted - request again
                //Returns a boolean
                //This will be called if the user clicks on 'Deny and do not show this again.' Then shouldShowRequestPermissionRationale returns false.
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestLocationPermission()
                } else { //user set the settings to 'Do not ask again'
                    //Toast.makeText(this, "You need location permission to use this app.", Toast.LENGTH_LONG).show()
                    //new method
                    showOverLay()
                }

            }

        }
    }

    private fun showOverLay() {
        overlay_view.visibility = View.VISIBLE
        overlay_view.animation = slideAnimation
    }

    private fun hideOverLay() {
        overlay_view.visibility = View.GONE
        overlay_view.animation = slideOutAnimation
    }
}

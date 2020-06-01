package com.example.dogs.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.dogs.R
import com.example.dogs.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var naveController : NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        naveController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, naveController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(naveController,null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_SEND_SMS ->{
                if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true)
                }else{
                    notifyDetailFragment(false)
                }
            }
        }
    }


    fun checkSmsPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){
                AlertDialog.Builder(this).setTitle(getString(R.string.smsPermissionTittle))
                    .setMessage(getString(R.string.smsPermissionMessage))
                    .setPositiveButton(getString(R.string.smsPermissionPositiveButtonLabel)){ _, _ ->
                        requestSmsPermission()
                    }
                    .setNegativeButton(getString(R.string.smsPermissionNegativeButtonLabel)){ _, _ ->
                        notifyDetailFragment(false)
                    }.show()
            }else{
                requestSmsPermission()
            }
        }else{
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS)
    }

    private fun notifyDetailFragment(permissionGaranted:Boolean){
        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment){
            (activeFragment as DetailFragment).onPermissionResult(permissionGaranted)
        }
    }
}

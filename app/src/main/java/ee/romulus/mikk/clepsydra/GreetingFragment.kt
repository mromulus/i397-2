package ee.romulus.mikk.clepsydra

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ee.romulus.mikk.clepsydra.models.AppViewModel
import kotlinx.android.synthetic.main.fragment_greeting.*

class GreetingFragment : Fragment() {
  private val requestPermissionCode = 10
  private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var vm: AppViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle? ): View? {

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_greeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    vm = ViewModelProviders.of(activity!!).get(AppViewModel::class.java)

    determineLocationEnabled()
    determinePermissions()

    observeValues()

    request_permissions.setOnClickListener { requestPermissions(requiredPermissions, requestPermissionCode) }
    request_location.setOnClickListener { startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
  }

  private fun determinePermissions() {
    vm.setPermissions(requiredPermissions.toList().all { hasPermission(it) })
  }

  private fun hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED
  }

  private fun determineLocationEnabled() {
    val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    vm.setLocationEnabled(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
  }

  private fun observeValues() {
    vm.locationEnabled.observe(this, Observer {
      if(it!!) {
        location_not_enabled.visibility = View.GONE
        request_location.visibility = View.GONE
      } else {
        location_not_enabled.visibility = View.VISIBLE
        request_location.visibility = View.VISIBLE
      }
    })

    vm.permissionGranted.observe(this, Observer {
      if(it!!) {
        permissions_not_enabled.visibility = View.GONE
        request_permissions.visibility = View.GONE
      } else {
        permissions_not_enabled.visibility = View.VISIBLE
        request_permissions.visibility = View.VISIBLE
      }
    })
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when(requestCode) {
      requestPermissionCode -> {
        determinePermissions()
        if(!grantResults.toList().all { it == PackageManager.PERMISSION_GRANTED }) {
          Toast.makeText(context, R.string.permissions_not_enabled, Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    determinePermissions()
    determineLocationEnabled()
  }
}

package ee.romulus.mikk.clepsydra

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_greeting.*

class GreetingFragment : Fragment() {
  private val requestPermissionCode = 10
  private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle? ): View? {

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_greeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    request_permissions.setOnClickListener { requestPermissions(requiredPermissions, requestPermissionCode) }

    checkPermissions()
  }

  private fun checkPermissions() {
    if(requiredPermissions.toList().all { hasPermission(it) }) {
      permissions_not_enabled.visibility = View.GONE
      request_permissions.visibility = View.GONE
//      showmap
    } else {
      permissions_not_enabled.visibility = View.VISIBLE
      request_permissions.visibility = View.VISIBLE
    }
  }

  private fun hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when(requestCode) {
      requestPermissionCode -> {
        if(grantResults.toList().all { it == PackageManager.PERMISSION_GRANTED }) {
          checkPermissions()
        } else {
          Toast.makeText(context!!, R.string.permissions_not_enabled, Toast.LENGTH_LONG).show()
        }
      }
    }
  }

}

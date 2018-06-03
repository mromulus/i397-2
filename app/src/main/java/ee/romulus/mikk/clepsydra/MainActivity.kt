package ee.romulus.mikk.clepsydra

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import ee.romulus.mikk.clepsydra.R.id.mapView

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

//      val model = ViewModelProviders.of(this).get(MapViewModel::class.java)
    Mapbox.getInstance(applicationContext, getString(R.string.access_token))

//    mapView.onCreate(savedInstanceState)

//        mapView.getMapAsync({})

  }

//    override fun onStart() {
//        super.onStart()
//        mapView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//
//    }
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//
//    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//
//    }
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
}

package ee.romulus.mikk.clepsydra

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import ee.romulus.mikk.clepsydra.models.GreetingViewModel

class MainActivity : FragmentActivity() {
  private lateinit var model: GreetingViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    model = ViewModelProviders.of(this).get(GreetingViewModel::class.java)

    insertGreeting()
    observeModel()
  }

  private fun insertGreeting() {
    val beginTransaction = supportFragmentManager.beginTransaction()
    beginTransaction.add(R.id.fragment_container, GreetingFragment())
    beginTransaction.commit()
  }

  private fun observeModel() {
    model.deviceReady.observe(this, Observer {
      if(it!!) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, MapFragment())
        transaction.commit()
      }
    })
  }
}

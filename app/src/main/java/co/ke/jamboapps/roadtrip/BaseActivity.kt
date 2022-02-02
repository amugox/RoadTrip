package co.ke.jamboapps.roadtrip

import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.ke.jamboapps.roadtrip.util.AppUtil

open class BaseActivity: AppCompatActivity() {

    fun setupHomeButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeFragment(fragment: Fragment) {
        Log.d("CLICK >>>", "Changing fragment......")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean) {
        Log.d("CLICK >>>", "Changing fragment......")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun showErrorMessage(message: String) {
        AppUtil.showToast(this, message)
    }
}
package eu.tutorials.myshoppal.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import eu.tutorials.myshoppal.R
import eu.tutorials.myshoppal.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefences = getSharedPreferences(Constants.MYSHOPPAL_PREFERENCE,Context.MODE_PRIVATE)
        val username = sharedPrefences.getString(Constants.LOGGED_IN_USERNAME,"")!!
        tv_main.text="Hello $username."
    }
}
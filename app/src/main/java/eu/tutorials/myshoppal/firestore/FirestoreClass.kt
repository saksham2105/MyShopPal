package eu.tutorials.myshoppal.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import eu.tutorials.myshoppal.activities.LoginActivity
import eu.tutorials.myshoppal.activities.RegisterActivity
import eu.tutorials.myshoppal.models.User
import eu.tutorials.myshoppal.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity,userInfo : User){

        mFirestore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{e ->
                activity.hideProgressDialog()
                Log.e(
                   activity.javaClass.simpleName,
                   "Error while registering the user.",
                    e
                )
            }
    }
    fun getCurrenUserID() : String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser !=null) {
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
    fun getUserDetails(activity: Activity){
        //Retrieving the data from Firestore Collection
        mFirestore.collection(Constants.USERS)
            .document(getCurrenUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have recieved snapshot which is converted to user data model object
                val user = document.toObject(User::class.java)!!
                val sharedPrefences =
                    activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCE,
                        Context.MODE_PRIVATE
                    )
                val editor : SharedPreferences.Editor = sharedPrefences.edit()
                //key : logged_in_username
                //value : value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                //Pass the result to Login Activity
                when(activity){

                    is LoginActivity ->{
                        //call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
                }
            }

    }
}
package eu.tutorials.myshoppal.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import eu.tutorials.myshoppal.R
import eu.tutorials.myshoppal.firestore.FirestoreClass
import eu.tutorials.myshoppal.models.ApiInterface
import eu.tutorials.myshoppal.models.MyDataItem
import eu.tutorials.myshoppal.models.User
import eu.tutorials.myshoppal.utils.MSPEditText
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

const val BASE_URL="https://jsonplaceholder.typicode.com/";
class LoginActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
        //getData();
        tv_register.setOnClickListener{
            val intent= Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
        btn_login.setOnClickListener{
            loginRegisteredUser()
        }
        tv_forgot_password.setOnClickListener{
            val intent= Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getData(){
        val retrofitBuilder=Retrofit.Builder().
        addConverterFactory(GsonConverterFactory.create()).
        baseUrl(BASE_URL).
        build().
        create(ApiInterface::class.java)

        val retrofitData=retrofitBuilder.getData()
        retrofitData.enqueue(object : Callback<List<MyDataItem>?> {
            override fun onResponse(
                call: Call<List<MyDataItem>?>,
                response: Response<List<MyDataItem>?>
            ) {
                val responseBody=response.body()!!
                var myStringBuilder=StringBuilder()
                for(myData in responseBody){
                  myStringBuilder.append(myData.id)
                  myStringBuilder.append("\n")
                }
                Log.d("LoginActivity","onResponse: "+myStringBuilder.toString())
            }

            override fun onFailure(call: Call<List<MyDataItem>?>, t: Throwable) {
              Log.d("LoginActivity","onFailure:"+t.message)
            }
        })
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
//                showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }
    // END

    private fun loginRegisteredUser(){
        if(validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim() {it <= ' '}
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
          FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).
                  addOnCompleteListener{task->
                      if(task.isSuccessful) {
                          FirestoreClass().getUserDetails(this@LoginActivity)
                      }
                      else {
                          hideProgressDialog()
                          showErrorSnackBar(task.exception!!.message.toString(),true)
                      }
                  }
        }
    }
    fun userLoggedInSuccess(user : User){

        hideProgressDialog()
        Log.i("First Name : ",user.firstName)
        Log.i("Last Name : ",user.lastName)
        Log.i("Email : ",user.email)

        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
        finish()
    }
}
package eu.tutorials.myshoppal.activities

import android.content.Intent
import android.os.Build

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import eu.tutorials.myshoppal.R
import eu.tutorials.myshoppal.firestore.FirestoreClass
import eu.tutorials.myshoppal.models.User
import eu.tutorials.myshoppal.utils.MSPButton
import eu.tutorials.myshoppal.utils.MSPEditText
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
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
        setupActionBar()
        //val tv_login : TextView =findViewById(R.id.tv_login)
        tv_login.setOnClickListener{
            val intent= Intent(this@RegisterActivity,LoginActivity::class.java)
            startActivity(intent)
        }
        val btn_register : MSPButton=findViewById(R.id.btn_register)
        btn_register.setOnClickListener{
            registerUser()
        }

    }
    private fun setupActionBar()
    {
        setSupportActionBar(findViewById(R.id.toolbar_register_activity))
        var actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        val toolbar_register_activity:Toolbar=findViewById(R.id.toolbar_register_activity)
        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        var et_first_name : MSPEditText=findViewById(R.id.et_first_name)
        var et_last_name : MSPEditText=findViewById(R.id.et_last_name)
        var et_email : MSPEditText=findViewById(R.id.et_email)
        var et_password : MSPEditText=findViewById(R.id.et_password)
        var et_confirm_password : MSPEditText=findViewById(R.id.et_confirm_password)
        var cb_terms_and_condition : AppCompatCheckBox=findViewById(R.id.cb_terms_and_condition)
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }
    /**
     * A function to register the user with email and password using FirebaseAuth.
     */
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {
            var et_email : MSPEditText=findViewById(R.id.et_email)
            var et_password : MSPEditText=findViewById(R.id.et_password)
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            var user = User(
                                firebaseUser.uid,
                                et_first_name.text.toString().trim { it <= ' '},
                                et_last_name.text.toString().trim { it <= ' '},
                                et_email.text.toString().trim { it <= ' '}
                            )
                            FirestoreClass().registerUser(this@RegisterActivity,user)
//                            FirebaseAuth.getInstance().signOut()
//                            finish()
                        } else {
                            hideProgressDialog()
                            // If the registering is not successful then show error message.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }
    // END
    fun userRegistrationSuccess(){

        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }

}
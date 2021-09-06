package eu.tutorials.myshoppal.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.myshoppal.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
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
    }
    private fun setupActionBar()
    {
        setSupportActionBar(toolbar_forgot_password_activity)
        var actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
        btn_submit.setOnClickListener{
            val email : String=et_email_forgot_pw.text.toString().trim() {it <= ' '}
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).
                addOnCompleteListener{task->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
            }
        }
    }

}
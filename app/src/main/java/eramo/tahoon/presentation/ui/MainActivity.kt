package eramo.tahoon.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.ActivityMainBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.ui.dialog.WarningDialog
import eramo.tahoon.util.Constants.TOPIC
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.MySingleton
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.notification.FirebaseMessageReceiver


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserUtil.init(this)
        LocalHelperUtil.init(this)
        LocalHelperUtil.loadLocal(this)
        LoadingDialog.init(this)
        WarningDialog.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        FirebaseMessageReceiver.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { firebaseToken ->
            FirebaseMessageReceiver.token = firebaseToken

            MySingleton.firebaseToken = firebaseToken

            Log.e("firebaseToken", firebaseToken)
        }

        //setup navStart
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        intent.extras?.let {
            UserUtil.setHasDeepLink(true)
        } ?: UserUtil.setHasDeepLink(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
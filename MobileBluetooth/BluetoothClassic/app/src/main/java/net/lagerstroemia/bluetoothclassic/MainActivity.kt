package net.lagerstroemia.bluetoothclassic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import net.lagerstroemia.bluetoothclassic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // 블루투스 권한 목록
    private val requiredPermissionList = mutableListOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
    }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // FloatingActionButton에서 블루투스 권한 체크
        binding.fab.setOnClickListener { view ->
            val isGranted: Boolean = checkAllPermission()
            if (!isGranted) {
                requestPermissionLauncher.launch(requiredPermissionList)
            }
            Snackbar.make(view, "Bluetooth Permission Granted:$isGranted", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    // 단순 권한이 승인되었는지 확인하는 함수
    private fun checkAllPermission(): Boolean = requiredPermissionList.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 권한 관리 런처
     * 단일: RequestPermission()
     * 복수: RequestMultiplePermission()
     *
     * 권한을 '허용 안함'한 경우
     * requestPermissionLauncher.. map:{
     * android.permission.BLUETOOTH=true,           // maxSdk을 30으로 설정하면 false로 반환된다.
     * android.permission.BLUETOOTH_ADMIN=true,
     * android.permission.BLUETOOTH_SCAN=false,
     * android.permission.BLUETOOTH_CONNECT=false,
     * android.permission.BLUETOOTH_ADVERTISE=false}
     *
     * 권한을 '허용'한 경우
     * requestPermissionLauncher.. map:{
     * android.permission.BLUETOOTH=true,
     * android.permission.BLUETOOTH_ADMIN=true,
     * android.permission.BLUETOOTH_SCAN=true,
     * android.permission.BLUETOOTH_CONNECT=true,
     * android.permission.BLUETOOTH_ADVERTISE=true}
     */
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        Log.i("HamHoney", "requestPermissionLauncher.. map:$it")
    }
}
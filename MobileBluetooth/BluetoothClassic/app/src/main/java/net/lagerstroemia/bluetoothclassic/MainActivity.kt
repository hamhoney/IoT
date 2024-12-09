package net.lagerstroemia.bluetoothclassic

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import net.lagerstroemia.bluetoothclassic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // 권한이 허용되었는지 알려주는 콜백인스턴스
    private var permissionCallback: ((isGranted: Boolean) -> Unit)? = null

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
            // val isGranted: Boolean = checkAllPermission()

            requestPermissionLauncher { isGranted ->
                Snackbar.make(view, "Bluetooth Permission Granted:$isGranted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        permissionCallback = null

        requestPermissionLauncher.unregister()
        requestAppSettingsLauncher.unregister()

        super.onDestroy()
    }

    // 단순 권한이 승인되었는지 확인하는 함수
    private fun checkAllPermission(): Boolean = requiredPermissionList.all {
        ContextCompat.checkSelfPermission(this@MainActivity, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * registerForActivityResult는 onCreate 이전에 초기화
     *
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
        Log.d("HamHoney", "permissionCallback:$permissionCallback")

        val isAllGranted = it.all { permission -> permission.value }

        if (isAllGranted) {
            permissionCallback?.let {
                it(isAllGranted)
            }
        } else {
            val dialog = AlertDialog.Builder(this@MainActivity)
                .setTitle("알람")
                .setMessage("블루투스('근처기기')권한이 필요합니다\n권한 설정창으로 이동하시겠습니까?\n(권한→근처기기)")
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        permissionCallback?.let {
                            it (false)
                        }
                        dialog?.dismiss()
                    }
                })
                .setPositiveButton("이동", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // OpenSetting - 한 번더 시스템 권한을 열어주도록 한다.
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }

                        // startActivity(intent)
                        requestAppSettingsLauncher.launch(intent)
                    }
                })
                .create()

            dialog.show()
        }
    }

    /**
     * 앱 설정창으로 이동한 뒤 다시 돌아오는 런처
     *
     * 그냥 돌아온 경우 반환 로그
     * requestAppSettingsLauncher.. map:ActivityResult{resultCode=RESULT_CANCELED, data=null}
     *
     * 권한('근처기기')을 허용하고 돌아왔을때 로그
     * requestAppSettingsLauncher.. map:ActivityResult{resultCode=RESULT_CANCELED, data=null}
     * */
    private val requestAppSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.i("HamHoney", "requestAppSettingsLauncher.. map:$it")

        permissionCallback?.let {
            it(checkAllPermission())
        }
    }

    // 권한 요청 함수
    private fun requestPermissionLauncher(callback: ((isGranted: Boolean) -> Unit)) {
        permissionCallback = callback      // ?:  - null인 경우 우측 값이 대입되도록 함

        requestPermissionLauncher.launch(requiredPermissionList)
    }
}
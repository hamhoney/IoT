package net.lagerstroemia.bluetoothclassic

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.lagerstroemia.bluetoothclassic.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // 블루투스 상태
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 블루투스 사용 가능여부 체크
        val bluetoothManager: BluetoothManager? = context?.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        // 블루투스 기기를 사용할 수 있으면 isEnabled = true
        // Device does support Bluetooth
        binding.buttonToggle.apply {
            isEnabled = bluetoothAdapter != null
            isChecked = bluetoothAdapter?.isEnabled ?: false
            setOnClickListener(toggleOnClickListener)
//            setOnCheckedChangeListener(toggleCheckedChangeListener)
        }
        Log.d("HamHoney", "This device supported Bluetooth:${binding.buttonToggle.isEnabled}")
    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(bluetoothStateReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(bluetoothStateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val toggleOnClickListener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            // toggle이벤트
            Log.d("HamHoney", "toggleOnClickListener.. onClick..")
            // 버튼 클릭해도 바뀌지 않도록
            // Programmatically하게
            binding.buttonToggle.isChecked = !binding.buttonToggle.isChecked

            if (bluetoothAdapter?.isEnabled == true) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("알람")
                    .setMessage("블루투스를 해제해주세요")
                    .setPositiveButton("확인", object: DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    }).create()

                dialog.show()

            } else {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothTurnLauncher.launch(enableBluetoothIntent)
            }
        }
    }

    // 블루투스 ON,OFF체크 리스너
    // onClick보다 먼저 호출된다.
    private val toggleCheckedChangeListener = object: CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            Log.d("HamHoney", "toggleCheckedChangeListener.. isChecked:$isChecked")

            // buttonView?.isChecked = !isChecked  // 자동으로 토글되는 것을 막는다.
        }
    }

    /**
     * Build.VERSION_CODES.S or below는 [disable]함수사용이 가능하다.
     * 블루투스 OFF는 무조건 유저가 직접해주어야 한다.
     * */
    private val bluetoothTurnLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("HamHoney", "bluetoothTurnLauncher.. StartActivityForResult:$it")

        // binding.buttonToggle.isChecked = bluetoothAdapter?.isEnabled ?: false
    }


    private val bluetoothStateReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Log.i("HamHoney","onReceive.. intent:$intent")

            val action = intent?.action

            action?.let {
                if (it == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    Log.i("HamHoney", "onReceive.. state:$state")
                    var isTurned = false
                    when (state) {
                        BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF -> isTurned = false
                        BluetoothAdapter.STATE_ON -> isTurned = true
                    }

                    binding.buttonToggle.isChecked = isTurned
                }
            }
        }
    }
}
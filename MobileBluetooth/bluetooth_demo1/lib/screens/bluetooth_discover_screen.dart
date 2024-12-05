import 'package:flutter/material.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:flutter_logcat/flutter_logcat.dart';

class BluetoothDiscoverScreen extends StatefulWidget {
  const BluetoothDiscoverScreen({super.key});

  @override
  State<BluetoothDiscoverScreen> createState() => _BluetoothDiscoverScreenState();
}

class _BluetoothDiscoverScreenState extends State<BluetoothDiscoverScreen> {
  List<BluetoothDevice> devices = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Bluetooth Discover Screen'),
      ),
      body: Center(
        child: Text('Discover Screen'),
      ),

      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          ElevatedButton(
            onPressed: () async {
              Log.d('Already SystemDevices onPressed..');
              // 	List of devices connected to the system, even by other apps
              try {
                var withServices = [Guid('180f')];    // Battery Level
                // [_BluetoothDiscoverScreenState:41] Aleary SystemDevices Exception:

                /// 아무런 권한을 주지 않고 [systemDevices]함수를 실행했을때 나온 결과 (Android환경)
                /// I/flutter (21948): PlatformException(getSystemDevices, FlutterBluePlus requires android.permission.BLUETOOTH_CONNECT permission, null, null)
                ///
                ///     <uses-permission android:name="android.permission.BLUETOOTH" />
                ///     <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
                ///
                /// 위의 권한만 준 뒤에 결과 확인
                /// > 실행되지 않는다.
                ///
                ///     <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
                /// 위의 권한 추가한 뒤 결과 확인
                /// > 근처 기기 권한을 찾는 안내창이 표출된다. 꼭 추가해주어야 하나보다.
                devices = await FlutterBluePlus.systemDevices(withServices);
                Log.i('devices:$devices');
                setState(() {

                });
              } catch (e) {
                Log.e('Aleary SystemDevices Exception:\n$e');
              }
            },
            child: Text('Already'),
          )
        ],
      ),
    );
  }
}

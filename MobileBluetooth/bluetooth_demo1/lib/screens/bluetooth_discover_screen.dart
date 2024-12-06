import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:flutter_logcat/flutter_logcat.dart';
import 'package:loading_progressbar/loading_progressbar.dart';

class BluetoothDiscoverScreen extends StatefulWidget {
  const BluetoothDiscoverScreen({super.key});

  @override
  State<BluetoothDiscoverScreen> createState() => _BluetoothDiscoverScreenState();
}

class _BluetoothDiscoverScreenState extends State<BluetoothDiscoverScreen> {
  /// 이미 연결된 디바이스를 보여주는 듯 함
  List<BluetoothDevice> connectedDevices = [];
  List<ScanResult> scanDevices = [];

  /// 스캔(Discover) 결과 목록 구독서비스
  late StreamSubscription<List<ScanResult>> scanDevicesSubscription;

  /// 현재 스캔 중인지 확인하기 위한 구독서비스
  late StreamSubscription<bool> scanningSubscription;

  /// 스캔 중일 때 LoadingProgressbar를 보여주기 위함
  final LoadingProgressbarController loadingProgressbarController = LoadingProgressbarController();

  @override
  void initState() {
    super.initState();

    /// 스캔한 디바이스의 항목
    scanDevicesSubscription = FlutterBluePlus.scanResults.listen((scanResult) {
      if (scanResult.length > 0 && scanResult.last.advertisementData.connectable) {
        Log.s('ScanDevices Subscription scanResult - device:${scanResult.last.device.remoteId}');

        if (scanResult.last.device.platformName.isNotEmpty) {
          Log.s('ScanDevices Subscription scanResult - platformName:${scanResult.last.device.platformName}');
          setState(() {
            scanDevices.add(scanResult.last);
          });
        }
      }
    }, onDone: () {
      Log.i('ScanDevices Subscription onDone..');
    }, onError: (e) {
      Log.w('ScanDevices Subscription Error:\n$e');
    });

    scanningSubscription = FlutterBluePlus.isScanning.listen((bool isScanning) {
      Log.x('Scanning Subscription isScanning:$isScanning');
      if (isScanning) {
        loadingProgressbarController.show();
      } else {
        loadingProgressbarController.hide();
      }
    }, onDone: () {
      Log.i('Scanning Subscription onDone..');
    }, onError: (e) {
      Log.e('Scanning Subscription onError..');
    });
  }

  @override
  void dispose() {
    scanDevicesSubscription.cancel();
    scanningSubscription.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return LoadingProgressbar(
      controller: loadingProgressbarController,
      progressbar: (context, progress) => CupertinoActivityIndicator(radius: 24.0,),
      child: Scaffold(
        appBar: AppBar(
          title: Text('Bluetooth Discover Screen'),
        ),
        body: ListView.builder(
          itemCount: scanDevices.length,
          itemBuilder: (context, index) {
            final BluetoothDevice device = scanDevices[index].device;
            return Card(
              child: ListTile(
                onTap: () {
              
                },
                leading: CircleAvatar(
                  child: Icon(Icons.bluetooth
                  ),
                ),
                title: Text(device.platformName),
                subtitle: Text(device.remoteId.str),
                trailing: Text(scanDevices[index].rssi.toString()),   // RSSI는 값이 낮을수록 신호가 약한 것
              ),
            );
          },
        ),
      
        floatingActionButton: Column(
          mainAxisAlignment: MainAxisAlignment.end,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            FloatingActionButton(
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
                  ///     + 설명) 이미 페어링된 블루투스 기기와 통신
                  /// 위의 권한 추가한 뒤 결과 확인
                  /// > 근처 기기 권한을 찾는 안내창이 표출된다. 꼭 추가해주어야 하나보다.
                  /// >> 그치만 스마트폰의 블루투스는 찾지 않았다.
                  ///
                  /// <uses-feature android:name="android.hardware.bluetooth" android:required="true"/>
                  /// 위의 권한을 추가한 뒤 결과 확인
                  /// >
                  connectedDevices = await FlutterBluePlus.systemDevices(withServices);
                  Log.i('connectedDevices:$connectedDevices');
                  setState(() {
      
                  });
                } catch (e) {
                  Log.e('Aleary SystemDevices Exception:\n$e');
                }
              },
              heroTag: null,
              child: Text('Already'),
            ),
            const SizedBox(height: 12.0),
            FloatingActionButton(
              onPressed: () async {
                Log.d('Discover Scanning onPressed..');
                scanDevices.clear();

                try {
                  const int scanningTimeSeconds = 30;

                  /// Discover Scanning Exceptio이 발생했다.
                  /// 아무래도 권한이 없어서 발생한 것 같다.
                  ///
                  ///     <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
                  /// 위의 권한을 추가하고 결과확인
                  /// > 스캔한다!
                  ///
                  /// 하지만, 아무런 디바이스도 스캔하지 못한다. 권한을 더 추가해주어야 하는 것 같다.
                  ///
                  ///  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
                  /// 권한을 추가하고 결과확인
                  /// > 찾지 못한다.
                  ///
                  /// <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
                  /// 저전력 블루투스를 사용하는 경우 선언해주어야 한다.
                  /// 결과는 어떨까?
                  /// > 검색한다...!!!!!!!
                  ///
                  /// BLE는 저전력이기 때문에 IoT, 모바일 앱에서만 사용이 가능하다.
                  await FlutterBluePlus.startScan(timeout: Duration(seconds: scanningTimeSeconds),
                    androidLegacy: true,
                    androidScanMode: AndroidScanMode.balanced,
                  );
                } catch (error, stackTrace) {
                  Log.w('Discover Scanning Exception:\n$stackTrace');
                }
              },
              heroTag: null,
              child: Text('Discover'),
            )
          ],
        ),
      ),
    );
  }
}

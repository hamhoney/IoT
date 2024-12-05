import 'dart:async';
import 'dart:io';

import 'package:animations/animations.dart';
import 'package:bluetooth_demo1/screens/bluetooth_discover_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';
import 'package:flutter_logcat/flutter_logcat.dart';

class BluetoothConnectScreen extends StatefulWidget {
  const BluetoothConnectScreen({super.key});

  @override
  State<BluetoothConnectScreen> createState() => _BluetoothConnectScreenState();
}

class _BluetoothConnectScreenState extends State<BluetoothConnectScreen> {
  // 블루투스 어댑터 상태
  BluetoothAdapterState adapterState = BluetoothAdapterState.unknown;
  // 블루투스 어댑터 실시간 상태 - 구독 서비스^^
  late StreamSubscription<BluetoothAdapterState> adapterStateSubscription;

  final String connectMsgOff = 'Bluetooth OFF';
  final String connectMsgOn = 'Bluetooth ON';
  late String connectMessage;

  @override
  void initState() {
    super.initState();
    Log.v('initState..');
    connectMessage = connectMsgOff;

    adapterStateSubscription = FlutterBluePlus.adapterState.listen((BluetoothAdapterState state) {
      adapterState = state;
      Log.s('BluetootAdapterState Subscription state:$state');
      // 메시지 갱신
      connectMessage = isConnected ? connectMsgOn : connectMsgOff;

      if (mounted) {
        setState(() {});
      }
    },);
  }

  @override
  void dispose() {
    Log.v('dispose..');
    // 블루투스 어댑터 상태 - 구독 취소
    adapterStateSubscription.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Bluetooth Connect Screen'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(connectMessage),
            Icon(isConnected ? Icons.bluetooth : Icons.bluetooth_disabled),
            ElevatedButton(
              onPressed: isConnected ? null : () async {
                // Request Bluetooth Connect
                Log.d('Bluetooth turnOn..');
                try {
                  if (Platform.isAndroid) {
                    await FlutterBluePlus.turnOn(timeout: 8);
                  }
                } catch (error, stackTrace) {
                  Log.w('Bluetooth turnOn Exception:\n$stackTrace');
                }
              },
              child: Text(isConnected ? 'ON' : 'OFF'),
            )
          ],
        ),
      ),
      floatingActionButton: Visibility(
        visible: isConnected,
        child: OpenContainer(
          transitionDuration: const Duration(milliseconds: 680),
          openBuilder: (context, action) {
            return BluetoothDiscoverScreen();
          },
          closedShape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12.0),
          ),
          closedColor: Theme.of(context).colorScheme.primaryContainer,
          closedBuilder: (context, action) {
            return SizedBox(
              width: 60.0,
              height: 60.0,
              child: Icon(Icons.navigate_next)
            );
          },
        ),
      ),
    );
  }

  // 디바이스 블루투스 ON / OFF 상태 체크
  bool get isConnected => adapterState == BluetoothAdapterState.on;
}

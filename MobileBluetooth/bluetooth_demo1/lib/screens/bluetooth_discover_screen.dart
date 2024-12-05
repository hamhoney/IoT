import 'package:flutter/material.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';

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
    );
  }
}

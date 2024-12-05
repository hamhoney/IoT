import 'package:flutter/material.dart';
import 'package:flutter_logcat/flutter_logcat.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({
    super.key,
    required this.title
  });

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  void initState() {
    super.initState();
    Log.v('initState..');
  }

  @override
  void dispose() {
    Log.v('dispose..');
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Log.v('build..');
    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Bluetooth Scanning

        },
        child: const Icon(Icons.play_arrow),
      ),
    );
  }
}

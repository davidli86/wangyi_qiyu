import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:wangyi_qiyu/wangyi_qiyu.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    WangyiQiyu().register('be719789c7111ff8e81109f90a2f54f9', 'test_app');
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await WangyiQiyu().platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(children: <Widget>[
            IconButton(icon: Icon(Icons.add), onPressed: _onPressed),
            IconButton(icon: Icon(Icons.local_gas_station), onPressed: _logout),
          ],),
        ),
      ),
    );
  }
  _onPressed() {
    var _defaultSessionConfig = {
      'sessionTitle': '在线客服',
      'groupId': 0,
      'staffId': 0,
      'robotId': 0,
      'vipLevel': 0,
      'openRobotInShuntMode': false,
      'commonQuestionTemplateId': 0,
      'source': {
        'title':'网易七鱼Flutter',
        'urlString':'http://www.qiyukf.com',
        'customInfo':'我是来自自定义的信息'
      },
    };
    var params = {
      'userId': 'uid10101010',
      'data': [
        {
          'key': 'real_name',
          'value': '边晨'
        },
        {
          'key': 'mobile_phone',
          'value': '13805713536',
          'hidden': false
        },
        {
          'key': 'email',
          'value': 'bianchen@163.com',
        },
      ]
    };
    WangyiQiyu().openServiceWindow(_defaultSessionConfig);
    WangyiQiyu().setUserInfo(params);
    WangyiQiyu().setCustomUIConfig({"navBackgroundColor":"#CC00FF"});
    print("code controller is here");
    WangyiQiyu().onUnreadCountChanged.listen((data) {
      print("onUnreadCountChanged: " + data.toString() );
    });
  }

  _logout() {
    WangyiQiyu().logout();
  }
}

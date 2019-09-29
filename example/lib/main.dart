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
          child: IconButton(icon: Icon(Icons.add), onPressed: _onPressed),
        ),
      ),
    );
  }
  _onPressed() {
    var _defaultSessionConfig = {
      'sessionTitle': 'QiYuDemoForFlutter',
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

      'commodityInfo': {
        'title':'Flutter商品',
        'desc':'这是来自网易七鱼Flutter的商品描述',
        'pictureUrlString':'https://qiyukf.nosdn.127.net/main/res/img/online/qy-web-cp-zxkf-kh-3.1.1-icon@2x_86ec48d3e30b8fe5d6c1d5099d370019.png',
        'urlString':'http://www.qiyukf.com',
        'note':'￥1888',
        'show':true,
        'tagsArray': [
          {
            'label': '1',
            'url': 'http://www.qiyukf.com',
            'focusIframe': '2',
            'data': '3'
          },
        ],
        'tagsString': 'tagsString',
        'isCustom': true,
        'sendByUser': false,
        'actionText': 'actionText',
        'actionTextColor': '#FFFFFF',
        'ext': '123456'
      },
      'buttonInfoArray': [
        {
          'buttonId': '123',
          'title': 'buttonInfoArray',
          'userData': 'userData'
        }
      ],
      'showCloseSessionEntry':true,
      'showQuitQueue':true
    };
    WangyiQiyu().openServiceWindow(_defaultSessionConfig);
    WangyiQiyu().setCustomUIConfig({"navBackgroundColor":"#CC00FF"});
  }

}

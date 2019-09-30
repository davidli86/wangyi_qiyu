package com.ddd.wangyi_qiyu;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;

import com.qiyukf.unicorn.api.*;
import com.qiyukf.unicorn.api.lifecycle.SessionLifeCycleListener;
import com.qiyukf.unicorn.api.lifecycle.SessionLifeCycleOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.FlutterException;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** QiyuPlugin */
public class WangyiQiyuPlugin implements MethodCallHandler {
  private Registrar _registrar;
  private YSFOptions _options;

//  EventSink buttonClickCallbackEvent;
//  EventSink onURLClickCallbackEvent;
//  EventSink onBotClickCallbackEvent;
//  EventSink onQuitWaitingCallbackEvent;
//  EventSink onPushMessageClickCallbackEvent;
//  EventSink onBotCustomInfoCallbackEvent;
//  EventSink unreadCountChangedEvent;
//  EventSink sessionListChangedEvent;
//  EventSink receiveMessageEvent;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    WangyiQiyuPlugin instance = new WangyiQiyuPlugin(registrar);
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "plugins.com.tmd/qiyu");
    channel.setMethodCallHandler(instance);
//    final EventChannel eventChannel = new EventChannel(registrar.messenger(), "plugins.com.tmd/event_qiyu");
//    eventChannel.setStreamHandler(instance);
  }

  public WangyiQiyuPlugin(Registrar registrar) {
    this._registrar = registrar;
    this._options = options();
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    HashMap arguments = (HashMap) call.arguments;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);

    } else if (call.method.equals("register")){
      String appKey = (String)arguments.get("appKey");
      String appName = (String)arguments.get("appName");
      Unicorn.init(_registrar.activity(), appKey, _options, new UILImageLoader());
      result.success(null);
    } else if (call.method.equals("openServiceWindow")) {
      ActivityManager manager = (ActivityManager) _registrar.context().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
      List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
      if (runningTasks != null && runningTasks.size() > 0) {
        ComponentName topActivity = runningTasks.get(0).topActivity;
        String topActivityClass = topActivity.getClassName();
        if(topActivityClass.equals("com.qiyukf.unicorn.ui.activity.ServiceMessageActivity")){
          result.success(null);
          return;
        }
      }
      ConsultSource source = null;
      String sessionTitle = null;
      if (arguments != null) {
        sessionTitle = arguments.get("sessionTitle") != null ? (String) arguments.get("sessionTitle") : "";
        Integer groupId = arguments.get("groupId") != null ? (Integer) arguments.get("groupId") : 0;
        Integer staffId = arguments.get("staffId") != null ? (Integer) arguments.get("staffId") : 0;
        Integer robotId = arguments.get("robotId") != null ? (Integer) arguments.get("robotId") : 0;
        Integer vipLevel = arguments.get("vipLevel") != null ? (Integer) arguments.get("vipLevel") : 0;
        boolean openRobotInShuntMode = arguments.get("openRobotInShuntMode") != null ? (boolean) arguments.get("openRobotInShuntMode") : false;
        HashMap sourceDict = (HashMap) arguments.get("source");
        String title = "";
        String urlString = "";
        String customInfo = "";
        if (sourceDict != null && !sourceDict.isEmpty()) {
          title = sourceDict.get("title") != null ? (String) sourceDict.get("title") : "";
          urlString = sourceDict.get("urlString") != null ? (String) sourceDict.get("urlString") : "";
          customInfo = sourceDict.get("customInfo") != null ? (String) sourceDict.get("customInfo") : "";
        }
        source = new ConsultSource(title, urlString, customInfo);
        source.groupId = groupId;
        source.staffId = staffId;
        source.robotId = robotId;
        source.vipLevel = vipLevel;
        source.robotFirst = openRobotInShuntMode;
      }
      Unicorn.openServiceActivity(_registrar.activity(), sessionTitle, source);
      result.success(null);
    } else if (call.method.equals("setUserInfo")) {
      String userId = arguments.get("userId") != null ? (String)arguments.get("userId") : null;
      String authToken = arguments.get("authToken") != null ? (String)arguments.get("authToken") : null;
      ArrayList dataArray = arguments.get("data") != null ?  (ArrayList)arguments.get("data") : null;
      if (userId != null || dataArray != null) {
        YSFUserInfo userInfo = new YSFUserInfo();
        userInfo.userId = userId;
        userInfo.authToken = authToken;
        if (dataArray != null && dataArray.size() > 0) {
          try {
            JSONArray mJSONArray = new JSONArray(dataArray);
            userInfo.data = mJSONArray.toString();
          } catch (Exception e) {
            System.out.println(e.toString());
          }
        }
        Unicorn.setUserInfo(userInfo);
      }
      result.success(null);
    } else if (call.method.equals("setCustomUIConfig")) {
      this.setCustomUIConfigWithDict(arguments);
      result.success(null);
    } else if ("getUnreadCount".equals(call.method)) {
      result.success(Unicorn.getUnreadCount());
    } else {
      result.notImplemented();
    }
  }

  private YSFOptions options() {
    YSFOptions options = new YSFOptions();
    options.statusBarNotificationConfig = new StatusBarNotificationConfig();
    return options;
  }

  private void setCustomUIConfigWithDict(HashMap arguments) {
    String navBackgroundColor = arguments.get("navBackgroundColor") != null ?
            (String)arguments.get("navBackgroundColor") : null;
    if (_options.uiCustomization == null) {
      _options.uiCustomization = new UICustomization();
    }
    if (navBackgroundColor != null) {
      _options.uiCustomization.titleBackgroundColor = Color.parseColor(navBackgroundColor);
    }
    Unicorn.updateOptions(_options);
  }

//  @Override
//  public void onListen(Object arguments, EventChannel.EventSink eventSink) {
//  }
//
//  @Override
//  public void onCancel(Object o) {
//  }
}


package com.ddd.wangyi_qiyu;

import android.app.Application;
import android.graphics.Color;

import com.qiyukf.unicorn.api.*;
import com.qiyukf.unicorn.api.lifecycle.SessionLifeCycleListener;
import com.qiyukf.unicorn.api.lifecycle.SessionLifeCycleOptions;

import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.HashMap;
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
  private boolean _isInSession;

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
      if (_isInSession) {
        result.success(null);
        return;
      }
      _isInSession = true;
      ConsultSource source = null;
      String sessionTitle = null;
      if (arguments != null) {
        sessionTitle = (String) arguments.get("sessionTitle");
        Integer groupId = (Integer) arguments.get("groupId");
        Integer staffId = (Integer) arguments.get("staffId");
        Integer robotId = (Integer) arguments.get("robotId");
        Integer vipLevel = (Integer) arguments.get("vipLevel");
        boolean openRobotInShuntMode = (boolean) arguments.get("openRobotInShuntMode");
        Integer commonQuestionTemplateId = (Integer) arguments.get("commonQuestionTemplateId");
        HashMap sourceDict = (HashMap) arguments.get("source");
        HashMap commodityInfoDict = (HashMap) arguments.get("commodityInfo");
        Array buttonInfoList = (Array) arguments.get("buttonInfoArray");

        String title = "";
        String urlString = "";
        String customInfo = "";
        if (sourceDict != null && !sourceDict.isEmpty()) {
          title = (String) sourceDict.get("title");
          urlString = (String) sourceDict.get("urlString");
          customInfo = (String) sourceDict.get("customInfo");
        }
        source = new ConsultSource(title, urlString, customInfo);
        source.groupId = groupId;
        source.staffId = staffId;
        source.robotId = robotId;
        source.vipLevel = vipLevel;
        source.robotFirst = openRobotInShuntMode;
        SessionLifeCycleOptions lifeCycleOptions = new SessionLifeCycleOptions();
        lifeCycleOptions.setCanCloseSession(true)
        .setCanQuitQueue(true);
        lifeCycleOptions.setSessionLifeCycleListener(new SessionLifeCycleListener() {
          @Override
          public void onLeaveSession() {
            _isInSession = false;
          }
        });
        source.sessionLifeCycleOptions = lifeCycleOptions;
      }
      Unicorn.openServiceActivity(_registrar.activity(), sessionTitle, source);
      result.success(null);
    } else if (call.method.equals("setUserInfo")) {
      String userId = (String)arguments.get("userId");
      String authToken = (String)arguments.get("authToken");
      Array dataArray = (Array)arguments.get("data");
      if (userId.length() > 0 || Array.getLength(dataArray) > 0) {
        YSFUserInfo userInfo = new YSFUserInfo();
        userInfo.userId = userId;
        userInfo.authToken = authToken;
        try {
          JSONArray mJSONArray = new JSONArray(dataArray);
          userInfo.data = mJSONArray.toString();
        } catch (Exception e) {
          System.out.println(e.toString());
        }
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
    String navBackgroundColor = (String)arguments.get("navBackgroundColor");
    if (_options.uiCustomization == null) {
      _options.uiCustomization = new UICustomization();
    }
    if (arguments.get("navBackgroundColor") != null) {
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


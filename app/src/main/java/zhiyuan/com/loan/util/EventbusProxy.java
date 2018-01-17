package zhiyuan.com.loan.util;

import org.greenrobot.eventbus.EventBus;

import zhiyuan.com.loan.bean.MessageEvent;

/**
 * @anthor haijun
 * @project name: Loan-master
 * @class name：zhiyuan.com.loan.util
 * @time 2018-01-15 10:56 AM
 * @describe
 */
public class EventbusProxy {
    private static EventbusProxy eventbusProxy;

    public static final int success = 1;
    public static final int fail = 2;


    public static EventbusProxy getInstance(){
        if (eventbusProxy==null){
            eventbusProxy = new EventbusProxy();
        }
        return eventbusProxy;
    }

    //消息类型
    public enum MessageEventType {
        msgType_RequestNetwork_APK,msgType_RequestNetwork_ArticleList
    }

    public void postDataOnBus(MessageEvent messageEvent) {
        EventBus.getDefault().post(messageEvent);
    }

    public void postDataOnBus(EventbusProxy.MessageEventType messageType,int singleValue,String DescriptionMsg,Object object) {
        MessageEvent messageEvent = new MessageEvent(messageType,singleValue,DescriptionMsg,object);
        EventBus.getDefault().post(messageEvent);
    }


}

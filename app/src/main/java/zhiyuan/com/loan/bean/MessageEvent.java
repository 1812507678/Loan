package zhiyuan.com.loan.bean;


import zhiyuan.com.loan.util.EventbusProxy;

/**
 * @anthor haijun
 * @project name: MyApplication
 * @class name：com.amsu.myapplication.bean
 * @time 2017-12-26 5:47 PM
 * @describe
 */
public class MessageEvent {
    public EventbusProxy.MessageEventType messageType;
    public int singleValue;
    public String DescriptionMsg;
    public Object object;


    public MessageEvent() {
    }

    public MessageEvent(EventbusProxy.MessageEventType messageType, int singleValue, String descriptionMsg, Object object) {
        this.messageType = messageType;
        this.singleValue = singleValue;
        DescriptionMsg = descriptionMsg;
        this.object = object;
    }
}

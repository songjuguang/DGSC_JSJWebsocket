package com.mkoteam.listener;


import com.mkoteam.entity.JSJConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ListenerManager {
    private Collection listeners;
    private static ListenerManager listenerManager;
    public static ListenerManager getSingle() {
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
        }
        return listenerManager;
    }

    /**
     * 添加事件
     */
    public void addDoorListener(DListener listener) {
        if (listeners == null) {
            listeners = new HashSet();
        }
        listeners.add(listener);
    }

    /**
     * 触发保存消息
     */
    public void saveMessage(String message, String groupId, JSJConfig js) {
        if (listeners == null) return;
        //ControllerApplication event = new ControllerApplication(this, message);
        JSJDataEvent event = new JSJDataEvent(message);
        notifyListeners(event, groupId, js);
    }

    /**
     * 通知所有的DoorListener
     */
    private void notifyListeners(JSJDataEvent event, String groupId, JSJConfig js) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            DListener listener = (DListener) iter.next();
            listener.doorEvent(event, groupId, js);
        }
    }


}
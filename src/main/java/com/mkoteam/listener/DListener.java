package com.mkoteam.listener;

import com.mkoteam.ControllerApplication;

import java.util.EventListener;

public interface DListener extends EventListener {
    public void doorEvent(JSJDataEvent event,String groupId);
}
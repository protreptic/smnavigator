package ru.magnat.smnavigator.synchronization.util;

public interface SynchronizationObserver {

	public void onStarted();
    public void onAck();
    public void onCompleted();
    public void onCanceled();
    public void onError();
    
}

package ru.magnat.smnavigator.synchronization.util;

public class SynchronizationObservable extends Observable<SynchronizationObserver> {

	public void notifyStarted() {
        synchronized(mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onStarted();
            }
        }
    }

    public void notifyAck() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onAck();
            }
        }
    }
    
    public void notifyCompleted() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onCompleted();
            }
        }
    }
    
    public void notifyCanceled() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onCanceled();
            }
        }
    }
    
    public void notifyError() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onError();
            }
        }
    }
    
}

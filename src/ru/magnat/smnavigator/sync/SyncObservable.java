package ru.magnat.smnavigator.sync;

public class SyncObservable extends Observable<SyncObserver> {

	public void notifyStatusChanged(SyncStatus status) {
        synchronized(mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onStatusChanged(status);
            }
        }
    }

}

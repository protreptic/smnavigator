package ru.magnat.smnavigator.sync.util;

import ru.magnat.smnavigator.sync.SyncStatus;

public interface SyncObserver {

	public void onStatusChanged(SyncStatus status);
    
}

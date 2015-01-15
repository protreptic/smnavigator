package ru.magnat.smnavigator.sync;

public interface SyncListener {
	public void onInitialSynchronizationCompleted(SyncStatus status);
	public void onSynchronizationCompleted(SyncStatus status);
}

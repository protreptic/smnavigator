package ru.magnat.smnavigator.synchronization;

public interface SynchronizationListener {
	public void onInitialSynchronizationCompleted(SynchronizationStatus status);
	public void onSynchronizationCompleted(SynchronizationStatus status);
}

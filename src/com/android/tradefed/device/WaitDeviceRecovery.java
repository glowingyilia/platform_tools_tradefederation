/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tradefed.device;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.android.tradefed.config.Option;
import com.android.tradefed.util.IRunUtil;
import com.android.tradefed.util.RunUtil;

import java.io.IOException;

/**
 * A simple implementation of a {@link IDeviceRecovery} that waits for device to be online and
 * respond to simple commands.
 */
public class WaitDeviceRecovery implements IDeviceRecovery {

    private static final String LOG_TAG = "WaitDeviceRecovery";

    /** the time in ms to wait before beginning recovery attempts for bootloader */
    // TODO: this is a gross hack - currently this value should be more than
    // DeviceManager.FASTBOOT_POLL_TIME because that factor drives the fastboot state refresh times
    private static final long INITIAL_BOOTLOADER_PAUSE_TIME =
        DeviceManager.FASTBOOT_POLL_WAIT_TIME * 3;

    @Option(name="device-wait-time",
            description="maximum time in ms to wait for a single device recovery command")
    protected long mWaitTime = 4 * 60 * 1000;

    /**
     * Get the {@link RunUtil} instance to use.
     * <p/>
     * Exposed for unit testing.
     */
    protected IRunUtil getRunUtil() {
        return RunUtil.getInstance();
    }

    /**
     * Sets the maximum time in ms to wait for a single device recovery command.
     */
    void setWaitTime(long waitTime) {
        mWaitTime = waitTime;
    }

    /**
     * {@inheritDoc}
     */
    public void recoverDevice(IDeviceStateMonitor monitor)
            throws DeviceNotAvailableException {
        // device may have just gone offline
        // sleep a small amount to give ddms state a chance to settle
        // TODO - see if there is better way to handle this
        Log.i(LOG_TAG, String.format("Pausing for %d for %s to recover",
                INITIAL_BOOTLOADER_PAUSE_TIME, monitor.getSerialNumber()));
        // wait for bootloader state instead of just sleeping to refresh state
        monitor.waitForDeviceBootloader(INITIAL_BOOTLOADER_PAUSE_TIME);

        if (monitor.getDeviceState() == TestDeviceState.FASTBOOT) {
            Log.i(LOG_TAG, String.format(
                    "Found device %s in fastboot but expected online. Rebooting...",
                    monitor.getSerialNumber()));
            // TODO: retry if failed
            getRunUtil().runTimedCmd(20*1000, "fastboot", "-s", monitor.getSerialNumber(),
                    "reboot");
        }

        // wait for device online
        IDevice device = monitor.waitForDeviceOnline(mWaitTime);
        if (device == null) {
            handleDeviceNotAvailable(monitor);
            return;
        }
        if (monitor.waitForDeviceAvailable(mWaitTime) == null) {
            // device is online but not responsive
            handleDeviceUnresponsive(monitor);
        }
    }

    /**
     * Handle situation where device is online but unresponsive.
     * @param monitor
     * @throws DeviceNotAvailableException
     */
    protected void handleDeviceUnresponsive(IDeviceStateMonitor monitor)
            throws DeviceNotAvailableException {
        // consider trying a reboot?
        throw new DeviceNotAvailableException(String.format(
                "Device %s is online but unresponsive", monitor.getSerialNumber()));
    }

    /**
     * Handle situation where device is not available.
     *
     * @param monitor the {@link IDeviceStateMonitor}
     * @throws DeviceNotAvailableException
     */
    protected void handleDeviceNotAvailable(IDeviceStateMonitor monitor)
            throws DeviceNotAvailableException {
        throw new DeviceNotAvailableException(String.format("Could not find device %s",
                monitor.getSerialNumber()));
    }

    /**
     * {@inheritDoc}
     */
    public void recoverDeviceBootloader(final IDeviceStateMonitor monitor)
            throws DeviceNotAvailableException {
        // device may have just gone offline
        // wait a small amount to give device state a chance to settle
        // TODO - see if there is better way to handle this
        Log.i(LOG_TAG, String.format("Waiting for %d for %s in bootloader",
                INITIAL_BOOTLOADER_PAUSE_TIME, monitor.getSerialNumber()));
        monitor.waitForDeviceBootloader(INITIAL_BOOTLOADER_PAUSE_TIME);

        if (monitor.getDeviceState() == TestDeviceState.ONLINE) {
            Log.i(LOG_TAG, String.format(
                    "Found device %s online but expected fastboot. Rebooting...",
                    monitor.getSerialNumber()));
            // TODO: retry if failed
            IDevice device = monitor.waitForDeviceOnline();
            if (device == null) {
                handleDeviceBootloaderNotAvailable(monitor);
                return;
            }
            rebootDeviceIntoBootloader(device);
        } else if (monitor.getDeviceState() == TestDeviceState.FASTBOOT) {
            Log.i(LOG_TAG, String.format(
                    "Found device %s in fastboot but unresponsive. Rebooting...",
                    monitor.getSerialNumber()));
            // TODO: retry
            getRunUtil().runTimedCmd(20*1000, "fastboot", "-s", monitor.getSerialNumber(),
                    "reboot-bootloader");
        }

        if (!monitor.waitForDeviceBootloader(mWaitTime)) {
            handleDeviceBootloaderNotAvailable(monitor);
        }
    }

    /**
     * Reboot device into bootloader.
     *
     * @param device the {@link IDevice} to reboot.
     */
    protected void rebootDeviceIntoBootloader(IDevice device) {
        try {
            device.reboot("bootloader");
        } catch (IOException e) {
            Log.w(LOG_TAG, String.format("failed to reboot %s", device.getSerialNumber()));
        }
    }

    /**
     * Handle situation where device is not available when expected to be in bootloader.
     *
     * @param monitor the {@link IDeviceStateMonitor}
     * @throws DeviceNotAvailableException
     */
    protected void handleDeviceBootloaderNotAvailable(final IDeviceStateMonitor monitor)
            throws DeviceNotAvailableException {
        throw new DeviceNotAvailableException(String.format(
                "Could not find device %s in bootloader", monitor.getSerialNumber()));
    }
}
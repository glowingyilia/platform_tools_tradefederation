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

package com.android.tradefed.invoker;

import com.android.tradefed.config.IConfiguration;
import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.device.ITestDevice;

/**
 * Handles one TradeFederation test invocation.
 */
public interface ITestInvocation {

    /**
     * Instantiate objects and perform the test invocation.
     *
     * @param device the {@link ITestDevice} to perform tests. May be <code>null</code> if tests to
     *            run are not dependent on a device
     * @param config the {@link IConfiguration} of this test run.
     * @throws DeviceNotAvailableException if communication with device was lost
     */
    public void invoke(ITestDevice device, IConfiguration config)
            throws DeviceNotAvailableException;

}
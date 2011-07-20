/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.android.tradefed.build;

import com.android.tradefed.config.Option;
import com.android.tradefed.config.OptionClass;
import com.android.tradefed.config.Option.Importance;
import com.android.tradefed.log.LogUtil.CLog;

import java.io.File;

/**
 * A {@link IBuildProvider} that constructs a {@link DeviceFolderBuildInfo} based on a provided
 * local path.
 */
@OptionClass(alias = "local-build")
public class LocalBuildProvider implements IBuildProvider {

    private static final String IMAGE_FILE_OPTION_NAME = "device-image-file";
    private static final String TEST_FILE_OPTION_NAME = "test-zip-file";
    private static final String DATA_FILE_OPTION_NAME = "user-data-file";

    @Option(name = "device-build-id", description = "the id of device build.")
    private Integer mBuildId = null;

    @Option(name = "test-target", description = "the test target name.")
    private String mTestTarget = null;

    @Option(name = "build-name", description = "the build name.")
    private String mBuildName = null;

    @Option(name = IMAGE_FILE_OPTION_NAME, description = "the device image file to use.",
            importance = Importance.IF_UNSET)
    private File mDeviceImageFile = null;

    @Option(name = TEST_FILE_OPTION_NAME, description = "the test zip file to use.")
    private File mTestsZipFile = null;

    @Option(name = DATA_FILE_OPTION_NAME, description = "the user data file to use.")
    private File mUserDataFile = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public IBuildInfo getBuild() throws BuildRetrievalError {
        if (mDeviceImageFile == null) {
            throw new IllegalArgumentException(String.format("missing --%s option",
                    IMAGE_FILE_OPTION_NAME));
        }
        if (!mDeviceImageFile.exists()) {
            throw new IllegalArgumentException(String.format("Local image path '%s' is invalid. " +
                    "Please provide a valid path via --%s", mDeviceImageFile.getAbsolutePath(),
                    IMAGE_FILE_OPTION_NAME));
        }
        DeviceBuildInfo buildInfo = null;

        if (mBuildId != null && mTestTarget != null && mBuildName != null) {
           buildInfo = new DeviceBuildInfo(mBuildId, mTestTarget, mBuildName);
        } else {
            buildInfo = new DeviceBuildInfo();
        }

        try {
            buildInfo.setDeviceImageFile(mDeviceImageFile);
            if (mTestsZipFile != null) {
                buildInfo.setTestsZipFile(mTestsZipFile);
            } else {
                CLog.d("Null Test Zip File, if you want to pass a test zip file, use --%s",
                        TEST_FILE_OPTION_NAME);
            }
            if (mUserDataFile != null) {
                buildInfo.setUserDataImageFile(mUserDataFile);
            } else {
                CLog.d("Null User Data File, if you want to pass a user data file, use --%s",
                        DATA_FILE_OPTION_NAME);
            }
            return buildInfo;
        } catch (RuntimeException e) {
            buildInfo.cleanUp();
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildNotTested(IBuildInfo info) {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUp(IBuildInfo info) {
        // ignore
    }
}
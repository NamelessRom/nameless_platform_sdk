# Copyright (C) 2015 The NamelessRom Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

# The Nameless Platform Framework Library
# ============================================================
include $(CLEAR_VARS)

nameless_src := src/java/namelessrom

LOCAL_MODULE := org.namelessrom.platform
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, $(nameless_src)) \

include $(BUILD_JAVA_LIBRARY)

# ====  org.namelessrom.platform.xml lib def  ========================
include $(CLEAR_VARS)

LOCAL_MODULE := org.namelessrom.platform.xml
LOCAL_MODULE_TAGS := optional

LOCAL_MODULE_CLASS := ETC

# This will install the file in /system/etc/permissions
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions

LOCAL_SRC_FILES := $(LOCAL_MODULE)

include $(BUILD_PREBUILT)

# The SDK
# ============================================================
include $(CLEAR_VARS)

nameless_src := src/java/namelessrom

LOCAL_MODULE := org.namelessrom.platform.sdk
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, $(nameless_src)) \

include $(BUILD_STATIC_JAVA_LIBRARY)

# build other packages
include $(call first-makefiles-under,$(LOCAL_PATH))
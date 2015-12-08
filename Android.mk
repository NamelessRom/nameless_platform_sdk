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

nameless_platform_res := APPS/org.namelessrom.platform-res_intermediates/src
nameless_src := library/src/main/java/

# The Nameless Platform Framework Library
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := org.namelessrom.platform
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, $(nameless_src)) \

namelessplat_LOCAL_INTERMEDIATE_SOURCES := \
    $(nameless_platform_res)/namelessrom/platform/R.java \
    $(nameless_platform_res)/namelessrom/platform/Manifest.java \
    $(nameless_platform_res)/org/namelessrom/platform/internal/R.java \

LOCAL_INTERMEDIATE_SOURCES := \
    $(namelessplat_LOCAL_INTERMEDIATE_SOURCES)

include $(BUILD_JAVA_LIBRARY)
nameless_framework_module := $(LOCAL_INSTALLED_MODULE)

# Make sure that R.java and Manifest.java are built before we build
# the source for this library.
nameless_framework_res_R_stamp := \
    $(call intermediates-dir-for,APPS,org.namelessrom.platform-res,,COMMON)/src/R.stamp
$(full_classes_compiled_jar): $(nameless_framework_res_R_stamp)
$(built_dex_intermediate): $(nameless_framework_res_R_stamp)

$(nameless_framework_module): | $(dir $(nameless_framework_module))org.namelessrom.platform-res.apk

nameless_framework_built := $(call java-lib-deps, org.namelessrom.platform)

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

LOCAL_MODULE := org.namelessrom.platform.sdk
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, $(nameless_src)) \

namelesssdk_LOCAL_INTERMEDIATE_SOURCES := \
    $(nameless_platform_res)/namelessrom/platform/R.java \
    $(nameless_platform_res)/namelessrom/platform/Manifest.java \

LOCAL_INTERMEDIATE_SOURCES := \
    $(namelesssdk_LOCAL_INTERMEDIATE_SOURCES)

$(full_target): $(nameless_framework_built) $(gen)
include $(BUILD_STATIC_JAVA_LIBRARY)

# full target for use by platform apps
#
include $(CLEAR_VARS)

LOCAL_MODULE:= org.namelessrom.platform.internal
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, $(nameless_src)) \

namelesssdk_LOCAL_INTERMEDIATE_SOURCES := \
    $(nameless_platform_res)/namelessrom/platform/R.java \
    $(nameless_platform_res)/namelessrom/platform/Manifest.java \
    $(nameless_platform_res)/org/namelessrom/platform/internal/R.java \
    $(nameless_platform_res)/org/namelessrom/platform/internal/Manifest.java

LOCAL_INTERMEDIATE_SOURCES := \
    $(namelesssdk_LOCAL_INTERMEDIATE_SOURCES)

$(full_target): $(nameless_framework_built) $(gen)
include $(BUILD_STATIC_JAVA_LIBRARY)

# build other packages
include $(call first-makefiles-under,$(LOCAL_PATH))

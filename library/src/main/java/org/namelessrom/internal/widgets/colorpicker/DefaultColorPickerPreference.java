/**
 * Copyright (C) 2015 The NamelessRom Project
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
package org.namelessrom.internal.widgets.colorpicker;

import android.annotation.ColorInt;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;

public class DefaultColorPickerPreference extends Preference {
    private int mCurrentColor;

    public DefaultColorPickerPreference(Context context) {
        super(context);
    }

    public DefaultColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DefaultColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setup(final Activity activity) {
        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                final Bundle args = new Bundle();
                args.putInt(DefaultColorPickerDialog.ARG_COLOR, mCurrentColor);

                final OnColorSelectedWrapper wrapper = new OnColorSelectedWrapper();
                wrapper.onColorListener = mOnColorListener;
                args.putSerializable(DefaultColorPickerDialog.ARG_LISTENER, wrapper);

                final DefaultColorPickerDialog dialog = new DefaultColorPickerDialog();
                dialog.setArguments(args);
                dialog.show(activity.getFragmentManager(), "");
                return false;
            }
        });
    }

    public int getCurrentColor() {
        return mCurrentColor;
    }

    public void setCurrentColor(@ColorInt int color) {
        mCurrentColor = color;
    }

    private final OnColorListener mOnColorListener = new OnColorListener() {
        @Override public void onColorChanged(int color) { }

        @Override public void onColorSelected(int color) {
            mCurrentColor = color;
            final OnPreferenceChangeListener listener = getOnPreferenceChangeListener();
            if (listener != null) {
                listener.onPreferenceChange(DefaultColorPickerPreference.this, mCurrentColor);
            }
        }
    };

    public static String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + alpha + red + green + blue;
    }

    public static int convertToColorInt(String argb) throws NumberFormatException {
        if (argb.startsWith("#")) {
            argb = argb.replace("#", "");
        }

        int alpha = -1, red = -1, green = -1, blue = -1;

        if (argb.length() == 8) {
            alpha = Integer.parseInt(argb.substring(0, 2), 16);
            red = Integer.parseInt(argb.substring(2, 4), 16);
            green = Integer.parseInt(argb.substring(4, 6), 16);
            blue = Integer.parseInt(argb.substring(6, 8), 16);
        } else if (argb.length() == 6) {
            alpha = 255;
            red = Integer.parseInt(argb.substring(0, 2), 16);
            green = Integer.parseInt(argb.substring(2, 4), 16);
            blue = Integer.parseInt(argb.substring(4, 6), 16);
        }

        return Color.argb(alpha, red, green, blue);
    }
}

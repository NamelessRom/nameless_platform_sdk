package org.namelessrom.internal.widgets.colorpicker;

import android.annotation.ColorInt;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.namelessrom.internal.widgets.colorpicker.sliders.LobsterShadeSlider;
import org.namelessrom.platform.internal.R;

public class DefaultColorPickerDialog extends DialogFragment {
    public static final String ARG_COLOR = "arg_color";
    public static final String ARG_LISTENER = "arg_listener";

    private int mInitialColor;
    private int mSelectedColor;
    private OnColorListener mColorListener;

    public DefaultColorPickerDialog() {
        super();
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        if (args != null) {
            if (mInitialColor != Color.WHITE) {
                mInitialColor = args.getInt(ARG_COLOR, Color.WHITE);
                mSelectedColor = mInitialColor;
            }
            if (mColorListener == null) {
                OnColorSelectedWrapper wrapper = (OnColorSelectedWrapper) args.get(ARG_LISTENER);
                if (wrapper != null) {
                    mColorListener = wrapper.onColorListener;
                }
            }
        }

        final Activity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, getTheme());
        builder.setTitle(R.string.pick_a_color);

        builder.setNegativeButton(getContext().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                if (mColorListener != null) {
                    mColorListener.onColorSelected(mSelectedColor);
                }
                dialogInterface.dismiss();
            }
        });

        final LayoutInflater inflater = LayoutInflater.from(activity);
        final LinearLayout root = (LinearLayout) inflater.inflate(R.layout.color_picker_dialog, null, false);
        builder.setView(root);

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LobsterPicker lobsterPicker = new LobsterPicker(getContext());
        root.addView(lobsterPicker, params);
        final LobsterShadeSlider shadeSlider = new LobsterShadeSlider(getContext());
        root.addView(shadeSlider, params);

        lobsterPicker.setColorHistoryEnabled(true);
        lobsterPicker.setHistory(mInitialColor);
        lobsterPicker.addDecorator(shadeSlider);

        lobsterPicker.addOnColorListener(new OnColorListener() {
            @Override public void onColorChanged(@ColorInt int color) { }

            @Override public void onColorSelected(@ColorInt int color) {
                mSelectedColor = color;
            }
        });

        return builder.create();
    }

}

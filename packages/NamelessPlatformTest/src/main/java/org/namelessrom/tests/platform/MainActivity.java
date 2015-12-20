package org.namelessrom.tests.platform;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import org.namelessrom.internal.BootDexoptDialog;
import org.namelessrom.internal.widgets.colorpicker.DefaultColorPickerDialog;
import org.namelessrom.internal.widgets.colorpicker.OnColorListener;
import org.namelessrom.internal.widgets.colorpicker.OnColorSelectedWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int MAX_DELAY_HANDLER = 5000;
    private static final int MIN_DELAY_HANDLER = 2000;

    private final Random mRandom = new Random();
    private final Handler mHandler = new Handler();

    private BootDexoptDialog mBootDexoptDialog;
    private int mCurrentType = Integer.MAX_VALUE;

    private final ArrayList<ApplicationInfo> applicationInfos = new ArrayList<>();

    private int mCurrentColor = Color.RED;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button launchDexoptDialog = (Button) findViewById(R.id.launch_dexopt_dialog);
        launchDexoptDialog.setOnClickListener(this);

        final Button launchColorPickerDialog = (Button) findViewById(R.id.launch_color_picker);
        launchColorPickerDialog.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.launch_color_picker: {
                final Bundle args = new Bundle();
                args.putInt(DefaultColorPickerDialog.ARG_COLOR, mCurrentColor);

                final OnColorSelectedWrapper wrapper = new OnColorSelectedWrapper();
                wrapper.onColorListener = mOnColorListener;
                args.putSerializable(DefaultColorPickerDialog.ARG_LISTENER, wrapper);

                final DefaultColorPickerDialog dialog = new DefaultColorPickerDialog();
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "");
                break;
            }
            case R.id.launch_dexopt_dialog: {
                launchDexOptDialog();
                break;
            }
        }
    }

    private final OnColorListener mOnColorListener = new OnColorListener() {
        @Override public void onColorChanged(int color) { }

        @Override public void onColorSelected(int color) {
            mCurrentColor = color;
        }
    };

    private void launchDexOptDialog() {
        final PackageManager packageManager = getPackageManager();
        final List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (final PackageInfo packageInfo : packageInfoList) {
            applicationInfos.add(packageInfo.applicationInfo);
        }

        mBootDexoptDialog = BootDexoptDialog.create(this, applicationInfos.size(), 0);
        mBootDexoptDialog.setCancelable(true);
        mHandler.post(mUpdateDexoptDialogRunnable);
    }

    final Runnable mUpdateDexoptDialogRunnable = new Runnable() {
        @Override public void run() {
            ApplicationInfo applicationInfo = null;
            int delay = MIN_DELAY_HANDLER / 2;
            switch (mCurrentType) {
                case Integer.MAX_VALUE: {
                    // upgrading fstrim
                    mCurrentType = Integer.MIN_VALUE + 1;
                    break;
                }
                case Integer.MIN_VALUE + 1: {
                    mCurrentType = 0;
                    // slip through
                }
                default: {
                    mCurrentType++;
                    if (mCurrentType > applicationInfos.size()) {
                        mCurrentType = Integer.MIN_VALUE + 2;
                    } else {
                        applicationInfo = applicationInfos.get(mCurrentType - 1);
                        delay = createHandlerDelay();
                    }
                    break;
                }
                case Integer.MIN_VALUE + 2: {
                    // upgrade complete
                    mCurrentType = Integer.MIN_VALUE + 3;
                    break;
                }
                case Integer.MIN_VALUE + 3: {
                    // starting apps
                    mCurrentType = Integer.MIN_VALUE;
                    break;
                }
                case Integer.MIN_VALUE: {
                    // we are done, dismiss
                    delay = 0;
                    mHandler.removeCallbacks(mUpdateDexoptDialogRunnable);
                    mHandler.postDelayed(new Runnable() {
                        @Override public void run() {
                            mBootDexoptDialog.dismiss();
                        }
                    }, createHandlerDelay() * 2);
                    break;
                }
            }

            mBootDexoptDialog.setProgress(applicationInfo, mCurrentType, applicationInfos.size());
            if (delay != 0) {
                mHandler.postDelayed(mUpdateDexoptDialogRunnable, delay);
            }
        }
    };

    private int createHandlerDelay() {
        final int randomDelay = mRandom.nextInt(MAX_DELAY_HANDLER);
        return Math.max(MIN_DELAY_HANDLER, randomDelay);
    }
}

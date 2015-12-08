package org.namelessrom.tests.platform;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import org.namelessrom.internal.BootDexoptDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private static final int MAX_DELAY_HANDLER = 5000;
    private static final int MIN_DELAY_HANDLER = 2000;

    private final Random mRandom = new Random();
    private final Handler mHandler = new Handler();

    private BootDexoptDialog mBootDexoptDialog;
    private int mCurrentType = Integer.MAX_VALUE;

    private final ArrayList<ApplicationInfo> applicationInfos = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PackageManager packageManager = getPackageManager();
        final List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (final PackageInfo packageInfo : packageInfoList) {
            applicationInfos.add(packageInfo.applicationInfo);
        }

        mBootDexoptDialog = BootDexoptDialog.create(this, applicationInfos.size(), 0);
        mHandler.postDelayed(mUpdateDexoptDialogRunnable, 0);
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

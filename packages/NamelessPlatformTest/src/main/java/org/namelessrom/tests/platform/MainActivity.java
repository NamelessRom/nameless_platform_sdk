package org.namelessrom.tests.platform;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;

import org.namelessrom.internal.BootDexoptDialog;

import java.util.Random;

public class MainActivity extends Activity {
    private static final int MAX_DELAY_HANDLER = 10000;
    private static final int MIN_DELAY_HANDLER = 2000;

    private static final int MAX_APPS_TO_DEXOPT = 20;

    private final Random mRandom = new Random();
    private final Handler mHandler = new Handler();

    private BootDexoptDialog mBootDexoptDialog;
    private int mCurrentType = Integer.MAX_VALUE;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBootDexoptDialog = new BootDexoptDialog(this, 0, MAX_APPS_TO_DEXOPT, 0);
        mHandler.postDelayed(mUpdateDexoptDialogRunnable, 500);
    }

    final Runnable mUpdateDexoptDialogRunnable = new Runnable() {
        @Override public void run() {
            ApplicationInfo applicationInfo = null;
            switch (mCurrentType) {
                case Integer.MAX_VALUE: {
                    // upgrading / starting android
                    mCurrentType = Integer.MIN_VALUE;
                    break;
                }
                case Integer.MIN_VALUE: {
                    // upgrading fstrim
                    mCurrentType = Integer.MIN_VALUE + 1;
                    break;
                }
                case Integer.MIN_VALUE + 1: {
                    mCurrentType = 0;
                    // slip through
                }
                default: {
                    applicationInfo = generateApplicationInfo();
                    mCurrentType++;
                    break;
                }
                case MAX_APPS_TO_DEXOPT: {
                    mCurrentType = Integer.MIN_VALUE + 2;
                    applicationInfo = generateApplicationInfo();
                    break;
                }
                case Integer.MIN_VALUE + 2: {
                    mCurrentType = Integer.MIN_VALUE + 3;
                    break;
                }
                case Integer.MIN_VALUE + 3: {
                    mHandler.removeCallbacks(mUpdateDexoptDialogRunnable);
                    mHandler.postDelayed(new Runnable() {
                        @Override public void run() {
                            mBootDexoptDialog.dismiss();
                        }
                    }, createHandlerDelay() * 2);
                }
            }

            mBootDexoptDialog.setProgress(applicationInfo, mCurrentType, MAX_APPS_TO_DEXOPT);
            mHandler.postDelayed(mUpdateDexoptDialogRunnable,
                    ((applicationInfo != null) ? createHandlerDelay() : MIN_DELAY_HANDLER / 2));
        }
    };

    private ApplicationInfo generateApplicationInfo() {
        // TODO: randomize more
        final ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = ((mCurrentType < 0) ? getPackageName() : getPackageName() + (mCurrentType + 1));
        applicationInfo.labelRes = R.string.app_name;
        applicationInfo.icon = R.mipmap.ic_launcher;
        return applicationInfo;
    }

    private int createHandlerDelay() {
        final int randomDelay = mRandom.nextInt(MAX_DELAY_HANDLER);
        return Math.max(MIN_DELAY_HANDLER, randomDelay);
    }
}

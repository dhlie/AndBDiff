package d.hl.andbdiff;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private String mOldApkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/V1.0.apk";
    private String mNewApkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/V2.0.apk";
    private String mPatchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/path.bf";
    private String mPatchedNewApkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/patchV2.0.apk";

    private TextView mTVInfo, mTVVersion;
    private boolean isDiffing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTVVersion = (TextView) findViewById(R.id.sample_text);
        mTVVersion.setText("Version:1.0");

        mTVInfo = (TextView) findViewById(R.id.info_text);
    }

    public void installOld(View view) {
        try {
            if (isDiffing) {
                Toast.makeText(this, "正在生成差分包...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mTVVersion.getText().toString().endsWith("1.0")) {
                Toast.makeText(this, "已经是旧版本", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(mOldApkPath);
            if (!file.exists()) {
                mTVInfo.setText("旧版本不存在");
                return;
            }
            installApk(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyDiff(View view) {
        try {
            if (isDiffing) {
                Toast.makeText(this, "正在生成差分包...", Toast.LENGTH_SHORT).show();
                return;
            }
            isDiffing = true;
            mTVInfo.setText("正在生成差分包...");


            new Thread(){
                @Override
                public void run() {
                    try {
                        PackageManager pm = getPackageManager();
                        final ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
                        File apk = new File(ai.sourceDir);
                        if (!apk.exists()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isDiffing = false;
                                    mTVInfo.setText(ai.sourceDir+" 不能打开");
                                }
                            });
                            return;
                        }
                        streamToFile(new FileInputStream(apk), mOldApkPath);

                        InputStream in = getAssets().open("v2.0.apk");
                        streamToFile(in, mNewApkPath);
                        File patch = new File(mPatchPath);
                        if (patch.exists()) patch.delete();
                        BDiffUtil.applyDiff(ai.sourceDir, mNewApkPath, mPatchPath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isDiffing = false;
                                mTVInfo.setText("差分包生成成功");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        isDiffing = false;
                        mTVInfo.setText("差分包生成失败");
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void applyPatch(View view) {
        if (isDiffing) {
            Toast.makeText(this, "正在生成差分包...", Toast.LENGTH_SHORT).show();
            return;
        }
        File patchFile = new File(mPatchPath);
        if (!patchFile.exists() || patchFile.length() <= 0) {
            mTVInfo.setText("差分包不存在");
            return;
        }
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            File file = new File(mPatchedNewApkPath);
            if (file.exists()) file.delete();
            BDiffUtil.applyPatch(ai.sourceDir, mPatchedNewApkPath, mPatchPath);
            file = new File(mPatchedNewApkPath);
            if (!file.exists()) {
                mTVInfo.setText("未合成正确的apk");
                return;
            }
            installApk(new File(mPatchedNewApkPath));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void installApk(File file) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(install);
    }


    private void streamToFile(InputStream in, String path) {
        if (in == null || path == null) return;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            if (!file.getParentFile().exists()) {
                return;
            }
        }
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            in.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

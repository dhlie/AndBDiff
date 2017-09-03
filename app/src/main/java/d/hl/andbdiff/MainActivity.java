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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private String mNewApkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/V2.0.apk";
    private String mPatchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/path.bf";
    private String mPatchedNewApkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bdiff/patchV2.0.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("Version:1.0");
    }

    public void applyDiff(View view) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            File apk = new File(ai.sourceDir);
            if (apk.exists()) {
                Toast.makeText(this, "存在", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream in = getAssets().open("v2.0.apk");
            streamToFile(in, mNewApkPath);
            BDiffUtil.applyDiff(ai.sourceDir, mNewApkPath, mPatchPath);
            Toast.makeText(this, "差分包生成成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void applyPatch(View view) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            BDiffUtil.applyPatch(ai.sourceDir, mPatchedNewApkPath, mPatchPath);
            installApk(new File(mPatchedNewApkPath));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void installApk(File file) {
        Intent intent=new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,3);
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

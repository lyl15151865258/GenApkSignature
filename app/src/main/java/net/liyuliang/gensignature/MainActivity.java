package net.liyuliang.gensignature;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private EditText edt;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt = findViewById(R.id.editText);
        tv = findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(onClickListener);
        tv.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    try {
                        String sign = generate(edt.getText().toString());
                        tv.setText(sign);
                        Log.e("gen_sign", sign);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(MainActivity.this, getString(R.string.CanNotFindPackage), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.textView:
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Signature", tv.getText());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(MainActivity.this, getString(R.string.Copied), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private String generate(String packageName) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        Signature[] signs = MainActivity.this.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
        if (signs.length > 0) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(signs[0].toByteArray());
            BigInteger bigInt = new BigInteger(1, md5.digest());
            return bigInt.toString(16);
        } else {
            return "";
        }
    }
}

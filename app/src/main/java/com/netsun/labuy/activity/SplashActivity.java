package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.utils.DownLoadAreaTask;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.NetworkUtils;
import com.netsun.labuy.utils.PublicFunc;

public class SplashActivity extends AppCompatActivity {

    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功
    private static final int OFFLINE = 2; // 如果支持离线阅读，进入离线模式
    private static final int SHOW_TIME_MIN = 800;
    private TextView mVersionNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mVersionNameText = (TextView) findViewById(R.id.version_name);
        mVersionNameText.setText(MyApplication.mVersionName);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int result = loadingCache();
                if (result == SUCCESS) {
                    String[] account = PublicFunc.getUserAndPassword();
                    if (account.length >= 2)
                        if (PublicFunc.login(account[0], account[1]))
                            PublicFunc.getReceiveAddressList();
                }
                long startTime = System.currentTimeMillis();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                new DownLoadAreaTask().execute(new Void[]{});
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            ;
        }.execute(new Void[]{});
    }

    private int loadingCache() {
        if (!NetworkUtils.checkNet(this))
            return OFFLINE;
        return SUCCESS;
    }

}

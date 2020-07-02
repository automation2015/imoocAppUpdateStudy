package auto.cn.imoocappupdate.update;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import auto.cn.imoocappupdate.R;
import auto.cn.imoocappupdate.update.bean.DownLoadBean;
import auto.cn.imoocappupdate.update.net.AppUpdater;
import auto.cn.imoocappupdate.update.net.INetCallback;
import auto.cn.imoocappupdate.update.net.INetDownloadCallback;
import auto.cn.imoocappupdate.update.ui.UpdateVersionShowDialog;
import auto.cn.imoocappupdate.update.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity{
    @Bind(R.id.btn_updater)
    Button btnUpdater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnUpdater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdater.getsInstance().getmNetManager().get("http://59.110.162.30/app_updater_version.json", new INetCallback() {
                    @Override
                    public void success(String response) {
                        Log.e("tag", "response=" + response);
                        //1.解析json
                        DownLoadBean bean=DownLoadBean.parse(response);
                        if(bean==null){
                            Toast.makeText(MainActivity.this,"版本检测接口返回数据异常！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //检测：是否需要弹框
                        try {
                            long versionCode = Long.parseLong(bean.getVersionCode());
                            if(versionCode<=Utils.getVersionCode(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"已经时最新版本，无需更新！！",Toast.LENGTH_SHORT).show();
                             return;}
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"版本检测接口返回版本号异常！",Toast.LENGTH_SHORT).show();
                        }
                        //3.弹框
                        UpdateVersionShowDialog.show(MainActivity.this,bean);
                        //2.版本匹配
                        //3.弹框
                        //4.点击下载

                    }

                    @Override
                    public void failed(Throwable throwable) {
                        Toast.makeText(MainActivity.this,"版本更新接口请求失败！",Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    }
                },MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUpdater.getsInstance().getmNetManager().cancel(this);
    }
}

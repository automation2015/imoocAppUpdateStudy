package auto.cn.imoocappupdate.update.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.Serializable;

import auto.cn.imoocappupdate.R;
import auto.cn.imoocappupdate.update.MainActivity;
import auto.cn.imoocappupdate.update.bean.DownLoadBean;
import auto.cn.imoocappupdate.update.net.AppUpdater;
import auto.cn.imoocappupdate.update.net.INetDownloadCallback;
import auto.cn.imoocappupdate.update.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdateVersionShowDialog extends DialogFragment {
    private static final String KEY_DOWNLOAD_BEAN = "download_bean";
    private DownLoadBean mDownloadBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            mDownloadBean= (DownLoadBean) arguments.getSerializable(KEY_DOWNLOAD_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update, container, false);
        bindEvents(view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置没有标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void bindEvents(View view) {
        TextView tvTitle=view.findViewById(R.id.tv_title);
        TextView tvContent=view.findViewById(R.id.tv_content);
        final TextView tvUpdate=view.findViewById(R.id.tv_update);
       tvTitle.setText(mDownloadBean.getTitle());
       tvContent.setText(mDownloadBean.getContent());
       tvUpdate.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(final View v) {
        //设置按钮不可重复点击
        v.setEnabled(false);
        final File targetFile=new File(getActivity().getCacheDir(),"target.apk");
        AppUpdater.getsInstance().getmNetManager().download(mDownloadBean.getUrl(), targetFile, new INetDownloadCallback() {
            @Override
            public void success(File apkFile) {
                //设置按钮可以点击
                v.setEnabled(true);
                //安装的代码
                Log.e("tag", "success=" + apkFile.getAbsolutePath());
                dismiss();
                //TOdO check md5
                String fileMd5=Utils.getFileMd5(targetFile);
                //logm 快捷键
                Log.e("tag", "success() called with: apkFile = [" + fileMd5 + "]");
                if(fileMd5!=null && fileMd5.equals(mDownloadBean.getMd5())) {
                    Utils.installApk(getActivity(), apkFile);
                }else{
                    Toast.makeText(getActivity(),"md5 检测失败！",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void progress(int progress) {
                //更新界面的代码
                Log.e("tag", "progress=" + progress);
                tvUpdate.setText(progress+"%");
            }

            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getActivity(),"文件下载失败！",Toast.LENGTH_SHORT).show();
                //设置按钮可以点击
                v.setEnabled(true);
            }
        },UpdateVersionShowDialog.this);
    }
});
    }
     //MainActivity满足条件显示dialog时将get获取的信息传递给dialog显示，并给dialog设置一个tag标志
    public static void show(FragmentActivity activity, DownLoadBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DOWNLOAD_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");
    }
    //dialog消失之后取消下载
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("tag", "onDismiss() called with: dialog = [onDismiss]");
        AppUpdater.getsInstance().getmNetManager().cancel(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

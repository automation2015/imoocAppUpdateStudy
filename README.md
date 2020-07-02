# imoocAppUpdateStudy
imooc 应用内版本更新
笔记：
1、/** N FileProvider件安装适配步骤：
     1）.AndroidManifest文件中添加
          <provider
               android:authorities="${applicationId}.fileprovider"//applicationId:包名
               android:name="android.support.v4.content.FileProvider"//固定写法
               android:exported="false"
               android:grantUriPermissions="true">
                   <meta-data android:name="android.support.FILE_PROVIDER_PATHS"//固定写法
                     android:resource="@xml/fileproviderpath"></meta-data>
            </provider>
      2）xml/fileproviderpath：res下新建xml文件夹，编写以下代码：
           <?xml version="1.0" encoding="utf-8"?>
            <paths xmlns:android="http://schemas.android.com/apk/res/android">
                //file path->content uri
                //xml文件映射了文件与uri之间的对应关系
                <root-path name="root"  path="."></root-path>
                <files-path name="files" path="."></files-path>
                //cachedir/targetFile->content://cache/targetFile
                //content://cache/targetFile->cache-path/targetFile->getCacheDir/targetFile
                //整个文件操作隐藏在contentprovider中
                <cache-path name="cache" path="."></cache-path>
                <external-cache-path name="external_cache" path="."></external-cache-path>
                <external-files-path name="external_file" path="."></external-files-path>
                <external-path  name="external" path="."></external-path>
            </paths>
        3）增加版本判断
             if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){//android 7.0
                        //fileprovider:AndroidManifest中定义的“authorities”
                        uri=FileProvider.getUriForFile(activity,activity.getPackageName()+".fileprovider",apkFile);
                       //添加读写权限
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                       intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                   }else{
                        uri=Uri.fromFile(apkFile);
                    }
2、 INSTALL PERMISSION的适配：
Manifest文件中增加权限<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
3、okhttp网络框架
   1）注意版本：最新版本仅支持 Android 5.0+ (API level 21+) and on Java 8+.，The OkHttp 3.12.x branch supports Android 2.3+ (API level 9+) and Java 7+.
   2）引入：implementation("com.squareup.okhttp3:okhttp:3.12.0")
4、android p不允许访问http开头的接口，需要配置网络安全策略
   1）Manifest中增加：android:networkSecurityConfig="@xml/network_security_config"
   2）res/xml/network_security_config：
   <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
          <base-config  cleartextTrafficPermitted="true"></base-config>
    </network-security-config>





package duanjie.projectframework;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
//import com.taobao.sophix.PatchStatus;
//import com.taobao.sophix.SophixManager;
//import com.taobao.sophix.listener.PatchLoadStatusListener;
import duanjie.projectframework.data.source.DaggerTasksRepositoryComponent;
import duanjie.projectframework.data.source.TasksRepositoryComponent;
import duanjie.projectframework.data.source.TasksRepositoryModule;
import timber.log.Timber;

/**
 * Created by Administrator on 2018/2/28.
 */

public class DJApplication extends Application{

    /**
     * 文件夹名称
     */
    public static final String APP_FILE_NAME = "DJFramework";

    private static DJApplication mApplication;

    private String appVersion;

    private TasksRepositoryComponent mRepositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Dagger初始化
        mRepositoryComponent = DaggerTasksRepositoryComponent.builder()
            .applicationModule(new ApplicationModule((getApplicationContext())))
            .tasksRepositoryModule(new TasksRepositoryModule()).build();
        //DJApplication初始化
        mApplication = this;


        // 日志工具
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // initialize最好放在attachBaseContext最前面，初始化直接在Application类里面，切勿封装到其他类
//        SophixManager.getInstance().setContext(this)
//            .setAppVersion(appVersion)
//            .setSecretMetaData("24815904","e165c8d628776885c96357a7f1471ae5","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")
//            .setAesKey(null)
//            .setEnableDebug(true)
//            .setPatchLoadStatusStub(new PatchLoadStatusListener() {
//                @Override
//                public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
//                    // 补丁加载回调通知
//                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
//                        // 表明补丁加载成功
//                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
//                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
//                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
//                    } else {
//                        // 其它错误信息, 查看PatchStatus类说明
//                    }
//                }
//            }).initialize();
//// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
//        SophixManager.getInstance().queryAndLoadNewPatch();
    }


    public static DJApplication getApplication() {
        return mApplication;
    }

    public TasksRepositoryComponent getTasksRepositoryComponent() {
        return mRepositoryComponent;
    }


}

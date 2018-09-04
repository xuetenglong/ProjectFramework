package duanjie.projectframework.home;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.DJApplication;
import duanjie.projectframework.R;
import duanjie.projectframework.data.BusResponse;
import duanjie.projectframework.data.LuckyDrawResponse;
import duanjie.projectframework.data.source.TasksDataSource;
import duanjie.projectframework.util.RxBus;
import io.reactivex.functions.Consumer;
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TasksDataSource mTasksRepository;

    private String appVersion;

    public static final String BUS_CONTRACT = "BUS_CONTRACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BusResponse bus1=new BusResponse(MainActivity.BUS_CONTRACT);
                bus1.id="段杰";
                Timber.e("段杰===================="+bus1.id);
                RxBus.getDefaultBus().send(bus1);
                PackageManager manager = MainActivity.this.getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                    appVersion = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this,ReleaseNoticeActivity.class));
            }
        });

        mTasksRepository = ((DJApplication)getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        LuckyDraw("duanjie","12");


        RxBus.getDefaultBus().toObserverable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Timber.e("=====call====" + o);
                if (o instanceof BusResponse) {
                    BusResponse busSelect = (BusResponse) o;
                    if (MainActivity.BUS_CONTRACT.equals(busSelect.bus)) {
                        String useId = busSelect.id;
                        Timber.e("====instanceof=====" + useId);
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e("=========" + throwable);
            }
        });
    }




    public void LuckyDraw(String name,String score) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("score", score);
        mTasksRepository.LuckyDrawSubimt(jsonObject)
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(new Consumer<LuckyDrawResponse>() {
                @Override
                public void accept(LuckyDrawResponse luckyDrawResponse) throws Exception {
                    Timber.e("=============luckyDrawResponse======="+JSONObject.toJSONString(luckyDrawResponse));
                    /**
                     * 保存数据
                     */
                    mTasksRepository.saveLuckyDraw(luckyDrawResponse);


                    /**
                     * 获取数据
                     */
                    mTasksRepository.getLuckyDraw();
                    Timber.e("=========获取数据============"+mTasksRepository.getLuckyDraw().id);
                }

            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });

    }

}

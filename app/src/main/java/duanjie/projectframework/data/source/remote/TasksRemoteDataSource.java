package duanjie.projectframework.data.source.remote;

import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.data.LuckyDrawResponse;
import duanjie.projectframework.data.source.TasksDataSource;
import javax.inject.Singleton;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
/**
 * Created by Administrator on 2018/2/28.
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Implementation of the data source that adds a latency simulating network.
 * 增加模拟网络延迟的数据源的实现。 网络请求获取数据
 */
@Singleton
public class TasksRemoteDataSource implements TasksDataSource {


    /**
     * 接口
     */
    private RequestService mService;

    /**
     * 超时的时候使用
     */
    private RequestService mTimeOutService;

    public TasksRemoteDataSource(){
         mService=RequestClient.newJsonClient();
        mTimeOutService=RequestClient.hasTimeOutClient();
    }


    @Override
    public Flowable<LuckyDrawResponse> LuckyDrawSubimt(JSONObject body) {
        return mService.luckyDrawSubimt(body).subscribeOn(Schedulers.io());
    }

    @Override
    public LuckyDrawResponse getLuckyDraw() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveLuckyDraw(LuckyDrawResponse luckyDraw) {
        throw new UnsupportedOperationException();
    }
}

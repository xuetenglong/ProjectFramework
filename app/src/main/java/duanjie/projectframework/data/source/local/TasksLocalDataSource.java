package duanjie.projectframework.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.data.LuckyDrawResponse;
import duanjie.projectframework.data.source.TasksDataSource;
import duanjie.projectframework.util.CacheUtil;
import io.reactivex.Flowable;
import javax.inject.Singleton;

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
 *
 *  * Concrete implementation of a data source as a db.
 *  作为DB的数据源的具体实现
 */
@Singleton
public class TasksLocalDataSource implements TasksDataSource {

    private Context mContext;

    public TasksLocalDataSource(@NonNull Context context) {
        mContext = context;
    }


    @Override
    public Flowable<LuckyDrawResponse> LuckyDrawSubimt(JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LuckyDrawResponse getLuckyDraw() {
        String[] keys = {LocalDataContract.LuckyDrawDataContract.JSON};
        String[] res = CacheUtil.getString(mContext, keys);
        LuckyDrawResponse luckyDraw = JSON.parseObject(res[0], LuckyDrawResponse.class);
        return luckyDraw != null ? luckyDraw : new LuckyDrawResponse();
    }

    @Override
    public void saveLuckyDraw(LuckyDrawResponse luckyDraw) {
        String[] keys = {LocalDataContract.LuckyDrawDataContract.JSON};
        String[] values = {JSON.toJSONString(luckyDraw)};
        CacheUtil.putString(mContext, keys, values);
    }


}

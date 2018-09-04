package duanjie.projectframework.data.source.remote;

import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.data.LuckyDrawResponse;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/2/28.
 *
 * Retrofit Interface
 * 接口
 */

interface RequestService {


    /**
     * 提交
     *
     * @return 响应
     */
    @POST("uploadData")
    Flowable<LuckyDrawResponse> luckyDrawSubimt(@Body JSONObject body);
}

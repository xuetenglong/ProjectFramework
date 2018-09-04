package duanjie.projectframework.data.source;

import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.data.LuckyDrawResponse;

import io.reactivex.Flowable;
import rx.Observer;


/**
 * Created by Administrator on 2018/2/28.
 访问任务数据的主要入口点。
 * Main entry point for accessing tasks data.
 */

public interface TasksDataSource {


    /**服务器
     * post hot
     * @param body
     * @return   Observer  Flowable
     */
    Flowable<LuckyDrawResponse> LuckyDrawSubimt(JSONObject body);


    /**
     * 本地获取抽奖的返回
     * @return
     */
    LuckyDrawResponse getLuckyDraw();

    /**
     * 本地保存抽奖的返回
     * @param luckyDraw
     */
    void saveLuckyDraw(LuckyDrawResponse luckyDraw);
}

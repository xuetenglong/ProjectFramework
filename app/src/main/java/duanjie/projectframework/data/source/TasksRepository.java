package duanjie.projectframework.data.source;

import com.alibaba.fastjson.JSONObject;
import duanjie.projectframework.data.LuckyDrawResponse;
import io.reactivex.Flowable;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Administrator on 2018/2/28.
 * Concrete implementation to load tasks from the data sources into a cache. <p> For simplicity,
 * this implements a dumb synchronisation between locally persisted data and data obtained from the
 * server, by using the remote data source only if the local database doesn't exist or is empty.
 * <p/>
 * By marking the constructor with {@code @Inject} and the class with {@code @Singleton}, Dagger
 * injects the dependencies required to create an instance of the TasksRespository (if it fails, it
 * emits a compiler error). It uses {@link TasksRepositoryModule} to do so, and the constructed
 * instance is available in {@link TasksRepositoryComponent}.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and therefore,
 * to ensure the developer doesn't instantiate the class manually and bypasses Dagger, it's good
 * practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
public class TasksRepository implements TasksDataSource{

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;


    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link TasksDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in {@link
     * TasksRepositoryModule}. <p> When two arguments or more have the same type, we must provide to
     * Dagger a way to differentiate them. This is done using a qualifier. <p> Dagger strictly
     * enforces that arguments not marked with {@code @Nullable} are not injected with {@code
     *
     * @Nullable} values.
     */
    @Inject
    TasksRepository(@Remote TasksDataSource tasksRemoteDataSource,
        @Local TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = tasksRemoteDataSource;
        mTasksLocalDataSource = tasksLocalDataSource;
    }

    @Override
    public Flowable<LuckyDrawResponse> LuckyDrawSubimt(JSONObject body) {
        return mTasksRemoteDataSource.LuckyDrawSubimt(body);
    }

    @Override
    public LuckyDrawResponse getLuckyDraw() {
        return mTasksLocalDataSource.getLuckyDraw();
    }

    @Override
    public void saveLuckyDraw(LuckyDrawResponse luckyDraw) {
        mTasksLocalDataSource.saveLuckyDraw(luckyDraw);
    }
}

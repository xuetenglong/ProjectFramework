package duanjie.projectframework.data.source;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import duanjie.projectframework.data.source.local.TasksLocalDataSource;
import duanjie.projectframework.data.source.remote.TasksRemoteDataSource;
import javax.inject.Singleton;

/**
 * Created by Administrator on 2018/2/28.
 * This is used by Dagger to inject the required arguments into the {@link TasksRepository}.
 */
@Module
public class TasksRepositoryModule {


    /**
     * 本地数据
     * @param context
     * @return
     */
    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context);
    }


    @Singleton
    @Provides
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new TasksRemoteDataSource();
    }

}

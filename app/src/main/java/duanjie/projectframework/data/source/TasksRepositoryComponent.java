package duanjie.projectframework.data.source;

import dagger.Component;
import duanjie.projectframework.ApplicationModule;
import javax.inject.Singleton;

/**
 * Created by Administrator on 2018/2/28.
 * This is a Dagger component. Refer to {@link  duanjie.projectframework.DJApplication} for the list of Dagger components used
 * in this application. <p> Even though Dagger allows annotating a {@link Component @Component} as a
 * singleton, the code itself must ensure only one instance of the class is created. This is done in
 * {@link duanjie.projectframework.DJApplication}.
 */
@Singleton
@Component(modules = {TasksRepositoryModule.class, ApplicationModule.class})
public interface TasksRepositoryComponent {
    TasksRepository getTasksRepository();
}

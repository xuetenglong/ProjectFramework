package duanjie.projectframework.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 发消息和接收消息
 * Created by Administrator on 2018/3/2.
 */

public class RxBus {

    private static RxBus defaultBus = new RxBus();

    public synchronized static RxBus getDefaultBus() {
        return defaultBus;
    }
    private final  PublishSubject<Object> subjects = PublishSubject.create();

    private final  Subject<Object,Object> subject = subjects.toSerialized();

    public boolean hasObservers() {
        return subjects.hasObservers();
    }
    public void send(Object o) {
       if (hasObservers()) {
            subject.onNext(o);
        }
    }

    public Observable<Object> toObserverable() {
        return subjects;
    }
}

/*
private static RxBus defaultBus = new RxBus();

    private final Subject<Object, Object> subject = new SerializedSubject<>(
        PublishSubject.create());

    private RxBus() {
    }

    public synchronized static RxBus getDefaultBus() {
        return defaultBus;
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public void send(Object o) {
        if (hasObservers()) {
            subject.onNext(o);
        }
    }

    public Observable<Object> toObserverable() {
        return subject;
    }
}*/

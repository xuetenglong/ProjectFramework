package duanjie.projectframework.home;

/**
 * Created by Administrator on 2018/9/3.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceView extends android.support.v7.widget.AppCompatTextView {
    private MediaPlayer mMediaPlayer = null;
    private Timer timer;
    private long countTimer;
    private long baseTimer;
    private Context context;

    public VoiceView(Context context) {
        super(context);
        initView(context);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    Handler startTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //播放状态
            if (countTimer == 0) {
                stopClock();
            }
            if (null != this) {
                setText((String) msg.obj);
            }
        }
    };
    private TimerTask timerTask;

    private void setTimerTask() {
        if (timerTask != null) return;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //播放状态
                countTimer --;
                if (countTimer >= 0) {
                    Message msg = new Message();
                    msg.obj = countTimer+"";
                    startTimeHandler.sendMessage(msg);
                }
            }
        };
    }


    private void initView(Context context) {
        this.context = context;
    }

    public void startPlaying(String path,long time) {
        this.countTimer = time;
        countTimer = countTimer/1000;
        this.baseTimer = time;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    stopClock();
                    if (timer == null) {
                        timer = new Timer("计时器");
                    }
                    setTimerTask();
                    timer.scheduleAtFixedRate(timerTask, 1000L, 1000L);
                }
            });
        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setText(baseTimer/1000+"");
                stopPlaying();
                onCompletionListener.onCompletion(mp);
            }
        });

    }
    private MediaPlayer.OnCompletionListener onCompletionListener;
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener){
        this.onCompletionListener = onCompletionListener;
    }

    public void stopPlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        stopClock();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlaying();
    }

    private void stopClock() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}

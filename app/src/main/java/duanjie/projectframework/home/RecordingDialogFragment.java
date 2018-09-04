package duanjie.projectframework.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import duanjie.projectframework.R;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by xuetenglong on 2017/5/15.
 */

public class RecordingDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String BAR_CODE_KEY = "BAR_CODE_KEY";
    private ImageView recordView;
    private TextView deleteView;
    private TextView finishView;
    private TextView contentView;
    private TextView titleView;
    //0 开始  1 录音中 2 录音结束 3 播放录音
    private int status = 0;
    private MediaPlayer mMediaPlayer = null;
    RecordingItem recordingItem;
    private long baseTimer;
    private Timer timer;
    private long recordTimer;
    private long countTimer;
    private long limitTime = 60000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeDialogTheme);
    }

    Handler startTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (status == 1) {
                //录音状态
                recordTimer = SystemClock.elapsedRealtime();
                if ((SystemClock.elapsedRealtime() - baseTimer) > limitTime) {
                    stopRecord();
                } else {
                    if (null != titleView) {
                        titleView.setText((String) msg.obj);
                    }
                }
            } else if (status == 3) {
                //播放状态
                long timeC = recordTimer - baseTimer;
                if (timeC < 0) {
                    stopPlaying();
                }else{
                    if (null != titleView) {
                        titleView.setText((String) msg.obj);
                    }
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AA000000")));
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fragment_record_dialog, container);
        recordView = view.findViewById(R.id.recordView);
        contentView = view.findViewById(R.id.contentView);
        deleteView = view.findViewById(R.id.deleteView);
        finishView = view.findViewById(R.id.finishView);
        recordView.setOnClickListener(this);
        deleteView.setOnClickListener(this);
        finishView.setOnClickListener(this);
        view.findViewById(R.id.closeView).setOnClickListener(this);
        titleView = view.findViewById(R.id.titleView);
        recordingItem = new RecordingItem();
        SharedPreferences sharePreferences = getActivity().getSharedPreferences("sp_name_audio", MODE_PRIVATE);
        final String filePath = sharePreferences.getString("audio_path", "");
        long elpased = sharePreferences.getLong("elpased", 0);
        recordingItem.setFilePath(filePath);
        recordingItem.setLength((int) elpased);
        timer = new Timer("计时器");
        titleView.setText("点击按钮开始录音");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordView:
                switch (status) {
                    case 0:
                        //录音中状态
                        onRecord();
                        break;
                    case 1:
                        //录音结束状态
                        stopRecord();
                        break;
                    case 2:
                        //播放录音状态
                        startPlaying();
                        break;
                    case 3:
                        //停止播放录音状态
                        stopPlaying();
                        break;
                }
                break;
            case R.id.deleteView:
                deleteView.setVisibility(View.GONE);
                finishView.setVisibility(View.GONE);
                recordView.setImageResource(R.mipmap.ic_microphone);
                status = 0;
                contentView.setText("最多可以录制一分钟");
                titleView.setText("点击按钮开始录音");
                stopMediaPlayer();
                break;
            case R.id.finishView:
                dismissAllowingStateLoss();
                String mFileName = getString(R.string.default_file_name)
                        + "_" + "luyin" + ".mp4";
                String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mFilePath += "/SoundRecorder/" + mFileName;
                EventBus.getDefault().post(new TimeEvent(mFilePath, 0));
                break;
            case R.id.closeView:
                dismissAllowingStateLoss();
                break;
        }


    }

    private void onRecord() {
        recordView.setImageResource(R.mipmap.ic_recording);
        contentView.setText("录制中");
        status = 1;
        deleteView.setVisibility(View.GONE);
        finishView.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), RecordingService.class);
//        Toast.makeText(getActivity(), "开始录音...", Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
        if (!folder.exists()) {
            //folder /SoundRecorder doesn't exist, create the folder
            folder.mkdir();
        }
        this.baseTimer = SystemClock.elapsedRealtime();
        stopClock();
        if (timer == null) {
            timer = new Timer("计时器");
        }
        setTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
        //start RecordingService
        getActivity().startService(intent);

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

    private void stopRecord() {
        recordView.setImageResource(R.mipmap.ic_record_finish);
        contentView.setText("录音完成");
        deleteView.setVisibility(View.VISIBLE);
        finishView.setVisibility(View.VISIBLE);
        status = 2;
        stopClock();
        Intent intent = new Intent(getActivity(), RecordingService.class);
//        Toast.makeText(getActivity(), "录音结束...", Toast.LENGTH_SHORT).show();
        getActivity().stopService(intent);
    }

    private TimerTask timerTask;

    private void setTimerTask() {
        if (timerTask != null) return;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (status == 1) {
                    //录音状态
                    int time = (int) ((SystemClock.elapsedRealtime() - baseTimer) / 1000);
                    String mm = new DecimalFormat("00").format(time % 3600 / 60);
                    String ss = new DecimalFormat("00").format(time % 60);
                    String timeFormat = new String(mm + ":" + ss);
                    Message msg = new Message();
                    msg.obj = timeFormat;
                    startTimeHandler.sendMessage(msg);
                } else if (status == 3) {
                    //播放状态
                    int time = (int) ((recordTimer - countTimer) / 1000);
                    countTimer += 1000;
                    String mm = new DecimalFormat("00").format(time % 3600 / 60);
                    String ss = new DecimalFormat("00").format(time % 60);
                    String timeFormat = new String(mm + ":" + ss);
                    Message msg = new Message();
                    msg.obj = timeFormat;
                    startTimeHandler.sendMessage(msg);
                }
            }
        };
    }


    private void startPlaying() {
        recordView.setImageResource(R.mipmap.ic_playing);
        contentView.setText("播放中");
        deleteView.setVisibility(View.VISIBLE);
        finishView.setVisibility(View.VISIBLE);
        status = 3;
        mMediaPlayer = new MediaPlayer();
        try {
            String mFileName = getString(R.string.default_file_name)
                    + "_" + "luyin" + ".mp4";
            String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    stopClock();
                    if (timer == null) {
                        timer = new Timer("计时器");
                    }
                    countTimer = baseTimer;
                    setTimerTask();
                    timer.scheduleAtFixedRate(timerTask, 0, 1000L);
                }
            });
        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recordView.setImageResource(R.mipmap.ic_record_finish);
                contentView.setText("播放完成");
                status = 2;
                stopPlaying();
            }
        });

    }

    private void stopPlaying() {
        status = 2;
        recordView.setImageResource(R.mipmap.ic_record_finish);
        contentView.setText("播放停止");
        deleteView.setVisibility(View.VISIBLE);
        finishView.setVisibility(View.VISIBLE);
        stopMediaPlayer();
    }
    private void stopMediaPlayer(){
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        stopClock();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
        stopRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
        stopRecord();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onRecord();
                }
                break;
        }
    }
}

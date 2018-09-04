package duanjie.projectframework.home;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import duanjie.projectframework.R;
import java.io.File;
import java.io.IOException;

/**
 * 录音的 Service
 * <p>
 * Created by developerHaoz on 2017/8/12.
 */

public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath() {
        mFileName = getString(R.string.default_file_name)
                + "_" + "luyin" + ".mp4";
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/SoundRecorder/" + mFileName;
        File dirs = new File(mFilePath);
        if (dirs.exists()){
            dirs.delete();
        }
    }

    public void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mRecorder = null;
                mRecorder = new MediaRecorder();
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            getSharedPreferences("sp_name_audio", MODE_PRIVATE)
                    .edit()
                    .putString("audio_path", mFilePath)
                    .putLong("elpased", mElapsedMillis)
                    .apply();

        }

    }
}
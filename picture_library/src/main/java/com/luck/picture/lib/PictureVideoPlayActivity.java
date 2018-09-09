package com.luck.picture.lib;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.rxbus2.RxBus;

import java.util.ArrayList;
import java.util.List;

public class PictureVideoPlayActivity extends PictureBaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private String video_path = "";
    private ImageView picture_left_back;
    private ImageView iv_play;
    private MediaController mMediaController;
    private VideoView mVideoView;
    private RelativeLayout sure_layout;
    private RelativeLayout delete_layout;
    private TextView notice_text;
    private int mPositionWhenPaused = -1;
    private boolean isPreview;
    private LocalMedia media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_video_play);
        video_path = getIntent().getStringExtra("video_path");
        media = getIntent().getParcelableExtra("video");
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        sure_layout =  findViewById(R.id.sure_layout);
        delete_layout =  findViewById(R.id.delete_layout);
        notice_text =  findViewById(R.id.notice_text);
        mVideoView.setBackgroundColor(Color.BLACK);
        mMediaController = new MediaController(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setVisibility(View.INVISIBLE);
        picture_left_back.setOnClickListener(this);
        findViewById(R.id.video_layout).setOnClickListener(this);
        findViewById(R.id.delete_text).setOnClickListener(this);
        findViewById(R.id.cancel_text).setOnClickListener(this);
        findViewById(R.id.sure_text).setOnClickListener(this);
        isPreview = getIntent().getBooleanExtra("is_preview",false);
        if(isPreview){
            iv_play.setVisibility(View.VISIBLE);
            delete_layout.setVisibility(View.GONE);
            if (media.getDuration() > 3 * 60 * 1000) {
                sure_layout.setVisibility(View.GONE);
                notice_text.setVisibility(View.VISIBLE);
            }else{
                sure_layout.setVisibility(View.VISIBLE);
            }
        }else{
            iv_play.setVisibility(View.GONE);
            sure_layout.setVisibility(View.GONE);
            delete_layout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onStart() {
        // Play Video
        mVideoView.setVideoPath(video_path);
        if(!isPreview){
            mVideoView.start();
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMediaController = null;
        mVideoView = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!isPreview){
            mVideoView.start();
        }else{
            iv_play.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back ||id == R.id.cancel_text) {
            finish();
        }else if (id == R.id.video_layout) {
            if(mVideoView==null)return;
            Log.i("video_layout","点击video_layout");
            if(mVideoView.isPlaying()){
                Log.i("video_layout","暂停");
                iv_play.setVisibility(View.VISIBLE);
                mVideoView.pause();
            }else{
                Log.i("video_layout","播放");
                iv_play.setVisibility(View.GONE);
                mVideoView.start();
            }
        }else if (id == R.id.delete_text) {
            RxBus.getDefault().post(new EventEntity(PictureConfig.EXTRA_DELETE_VIDEO));
            finish();
        }else if (id == R.id.sure_text) {
            List<LocalMedia> images = new ArrayList<>();
            images.add(media);
            RxBus.getDefault().post(new EventEntity(PictureConfig.EXTRA_DELETE_VIDEO,images));
            finish();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)) {
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started
                    mVideoView.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                }
                return false;
            }
        });
    }
}

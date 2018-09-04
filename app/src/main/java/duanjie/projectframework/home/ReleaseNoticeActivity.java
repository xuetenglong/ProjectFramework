package duanjie.projectframework.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.suke.widget.SwitchButton;
import duanjie.projectframework.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 发布通知
 * Created by Administrator on 2018/9/3.
 */

public class ReleaseNoticeActivity extends AppCompatActivity implements SwitchButton.OnCheckedChangeListener, View.OnClickListener{
    SwitchButton mSwitchButton1,mSwitchButton2;
    private VoiceView voiceView;
    private TextView pathView;
    private boolean isPlay;
    private String url;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_notice);
        EventBus.getDefault().register(this);

        TextView mLeftView = (TextView) findViewById(R.id.title_default_left);
        TextView mMiddleView = (TextView) findViewById(R.id.title_default_middle);
        TextView mRightView = (TextView) findViewById(R.id.title_default_right);
        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReleaseNoticeActivity.this,StudentReleaseTaskActivity.class));
            }
        });


        mSwitchButton1=(SwitchButton)findViewById(R.id.switchButton1);
        mSwitchButton2=(SwitchButton)findViewById(R.id.switchButton1);

        /**
         * 通知标题
         */
        TextView mNoticeTitle = (TextView) findViewById(R.id.ed_notice_title);
        SpannableString sNoticeTitle = new SpannableString("通知标题(必填)...");//定义hint的值
        AbsoluteSizeSpan aNoticeTitle = new AbsoluteSizeSpan(15,true);//设置字体大小 true表示单位是sp
        sNoticeTitle.setSpan(aNoticeTitle, 0, sNoticeTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNoticeTitle.setHint(new SpannedString(sNoticeTitle));

        mLeftView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mLeftView.setText("取消");
        mMiddleView.setText("发布通知");
        mRightView.setText("发布");



        findViewById(R.id.ly_record).setOnClickListener(this);
        pathView = findViewById(R.id.pathView);
        voiceView = findViewById(R.id.voiceView);
        voiceView.setOnClickListener(this);


        final int maxNum = 200;
        final TextView leftNum = (TextView) findViewById(R.id.leftNum);
        EditText ed = (EditText) findViewById(R.id.ed_notice_content);
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(maxNum-s.length()==0 || maxNum-s.length()<0){
                    Toast.makeText(ReleaseNoticeActivity.this,"不能超过200个文字",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    leftNum.setText((maxNum-s.length())+"");
                }

            }
        });

    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (view.isChecked()){
            mSwitchButton1.setChecked(true);
            mSwitchButton2.setChecked(true);
        }else{
            mSwitchButton1.setChecked(false);
            mSwitchButton2.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_record:
                final RecordingDialogFragment fragment = new RecordingDialogFragment();
                fragment.show(getSupportFragmentManager(), RecordingDialogFragment.class.getSimpleName());
                fragment.setCancelable(false);
                break;
            case R.id.voiceView:
                if (!TextUtils.isEmpty(url)) {
                    if (isPlay) {
                        voiceView.stopPlaying();
                        isPlay = false;
                    } else {
                        voiceView.startPlaying(url, time);
                        isPlay = true;
                    }

                }
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TimeEvent timeEvent) {
        SharedPreferences sharePreferences = this.getSharedPreferences("sp_name_audio", MODE_PRIVATE);
        final String filePath = sharePreferences.getString("audio_path", "");
        long elpased = sharePreferences.getLong("elpased", 0);
        url = filePath;
        this.time = elpased;
        pathView.setText(TextUtils.isEmpty(url) ? "" : url);
        voiceView.setText((this.time/1000)+"");
        voiceView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlay = false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

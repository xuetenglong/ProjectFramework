package duanjie.projectframework.home;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.suke.widget.SwitchButton;
import duanjie.projectframework.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 发布任务
 * Created by Administrator on 2018/9/4.
 */

public class StudentReleaseTaskActivity extends AppCompatActivity implements View.OnClickListener{
    private VoiceView voiceView;
    private TextView pathView;
    private boolean isPlay;
    private String url;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_release_task);
        EventBus.getDefault().register(this);

        TextView mLeftView = (TextView) findViewById(R.id.title_default_left);
        TextView mMiddleView = (TextView) findViewById(R.id.title_default_middle);
        TextView mRightView = (TextView) findViewById(R.id.title_default_right);
        mRightView.setTextColor(getResources().getColor(R.color.c3677DA));


        mLeftView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mLeftView.setText("取消");
        mMiddleView.setText("发布任务");
        mRightView.setText("提交");

        findViewById(R.id.ly_record).setOnClickListener(this);
        pathView = findViewById(R.id.pathView);
        voiceView = findViewById(R.id.voiceView);
        voiceView.setOnClickListener(this);
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

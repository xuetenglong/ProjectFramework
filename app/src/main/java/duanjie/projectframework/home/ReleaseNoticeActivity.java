package duanjie.projectframework.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.PictureVideoPlayActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.rxbus2.RxBus;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import duanjie.projectframework.R;

/**
 * 发布通知
 * Created by Administrator on 2018/9/3.
 */

public class ReleaseNoticeActivity extends AppCompatActivity implements SwitchButton.OnCheckedChangeListener, View.OnClickListener {
    SwitchButton mSwitchButton1, mSwitchButton2;
    private VoiceView voiceView;
    private TextView pathView;
    private boolean isPlay;
    private String url;
    private long time;
    private List<LocalMedia> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView videoView;
    private RelativeLayout playLayout;
    private GridImageAdapter adapter;
    List<LocalMedia> localMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_notice);
        EventBus.getDefault().register(this);
        RxBus.getDefault().register(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(ReleaseNoticeActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(ReleaseNoticeActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(9);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            PictureSelector.create(ReleaseNoticeActivity.this).themeStyle(R.style.picture_QQ_style).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(ReleaseNoticeActivity.this).externalPictureVideo(media.getPath());
                            break;
                    }
                }
            }
        });
        TextView mLeftView = (TextView) findViewById(R.id.title_default_left);
        TextView mMiddleView = (TextView) findViewById(R.id.title_default_middle);
        TextView mRightView = (TextView) findViewById(R.id.title_default_right);
        videoView = findViewById(R.id.videoView);
        playLayout = findViewById(R.id.playLayout);
        playLayout.setVisibility(View.GONE);
        playLayout.setOnClickListener(this);
        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReleaseNoticeActivity.this, StudentReleaseTaskActivity.class));
            }
        });


        mSwitchButton1 = (SwitchButton) findViewById(R.id.switchButton1);
        mSwitchButton2 = (SwitchButton) findViewById(R.id.switchButton1);

        /**
         * 通知标题
         */
        TextView mNoticeTitle = (TextView) findViewById(R.id.ed_notice_title);
        SpannableString sNoticeTitle = new SpannableString("通知标题(必填)...");//定义hint的值
        AbsoluteSizeSpan aNoticeTitle = new AbsoluteSizeSpan(15, true);//设置字体大小 true表示单位是sp
        sNoticeTitle.setSpan(aNoticeTitle, 0, sNoticeTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNoticeTitle.setHint(new SpannedString(sNoticeTitle));

        mLeftView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mLeftView.setText("取消");
        mMiddleView.setText("发布通知");
        mRightView.setText("发布");


        findViewById(R.id.ly_record).setOnClickListener(this);
        findViewById(R.id.pictureLayout).setOnClickListener(this);
        findViewById(R.id.allClassView).setOnClickListener(this);
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
                if (maxNum - s.length() == 0 || maxNum - s.length() < 0) {
                    Toast.makeText(ReleaseNoticeActivity.this, "不能超过200个文字", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    leftNum.setText((maxNum - s.length()) + "");
                }

            }
        });

    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (view.isChecked()) {
            mSwitchButton1.setChecked(true);
            mSwitchButton2.setChecked(true);
        } else {
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
            case R.id.pictureLayout:
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(ReleaseNoticeActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(R.style.picture_QQ_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(9)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .isCamera(false)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .selectionMedia(selectList)
                        .forResult(PictureConfig.CHOOSE_PICTURE_REQUEST);//结果回调onActivityResult code
                break;
            case R.id.allClassView:
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(R.style.picture_QQ_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(9)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .previewVideo(true)// 是否可预览图片
                        .isCamera(false)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .forResult(PictureConfig.CHOOSE_VIDEO_REQUEST);//结果回调onActivityResult code
                break;
            case R.id.playLayout:
                if(localMedia!=null&&localMedia.size()>0){
                    Intent intent = new Intent(this,PictureVideoPlayActivity.class);
                    Bundle bundle= new Bundle();
                    bundle.putString("video_path", localMedia.get(0).getPath());
                    intent.putExtras(bundle);
                    startActivity(intent);
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
        voiceView.setText((this.time / 1000) + "");
        voiceView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlay = false;
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
// 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(ReleaseNoticeActivity.this)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_QQ_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(9)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .isCamera(false)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .selectionMedia(selectList)
                    .forResult(PictureConfig.CHOOSE_PICTURE_REQUEST);//结果回调onActivityResult code

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_PICTURE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
                case PictureConfig.CHOOSE_VIDEO_REQUEST:
                    playLayout.setVisibility(View.VISIBLE);
                    localMedia = PictureSelector.obtainMultipleResult(data);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(this)
                            .load(localMedia.get(0).getPath())
                            .apply(options)
                            .into(videoView);
                    break;
            }
        }
    }
    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @com.luck.picture.lib.rxbus2.Subscribe(threadMode = com.luck.picture.lib.rxbus2.ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.EXTRA_SELECT_LIST_INT:
                selectList = obj.medias;
                for (LocalMedia media : selectList) {
                    Log.i("图片-----》", media.getPath());
                }
                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
                break;
            case PictureConfig.EXTRA_DELETE_VIDEO:
                playLayout.setVisibility(View.GONE);
                break;
            case PictureConfig.CHOOSE_VIDEO_REQUEST:
                playLayout.setVisibility(View.VISIBLE);
                selectList = obj.medias;
                for (LocalMedia media : selectList) {
                    Log.i("图片-----》", media.getPath());
                }
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(this)
                        .load(localMedia.get(0).getPath())
                        .apply(options)
                        .into(videoView);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        RxBus.getDefault().unregister(this);
    }
}

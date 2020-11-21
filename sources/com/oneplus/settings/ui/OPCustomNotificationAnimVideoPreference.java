package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0016R$raw;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.AspectRatioFrameLayout;
import com.oneplus.settings.ui.OPCustomNotificationAnimVideoPreference;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OPCustomNotificationAnimVideoPreference extends Preference {
    private boolean mAnimationAvailable;
    private List<OPCustomItemEntity> mAnims = new ArrayList();
    private float mAspectRadio = 1.0f;
    private final Context mContext;
    private OPCustomItemEntityViewHolder mCurrentVH;
    private int mLastIndex;
    private MediaPlayer mMediaPlayer;
    private int mPreviewResource;
    private int mSelectedIndex;
    private SettingsPreferenceFragment mSettingsPreferenceFragment;
    private Uri mVideoPath;
    private boolean mVideoPaused;
    private boolean mVideoReady;

    public void setSettingsPreferenceFragment(SettingsPreferenceFragment settingsPreferenceFragment) {
        this.mSettingsPreferenceFragment = settingsPreferenceFragment;
    }

    public OPCustomNotificationAnimVideoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mLastIndex = Settings.System.getIntForUser(context.getContentResolver(), "op_custom_horizon_light_animation_style", 0, -2);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        try {
            obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_animation, 0);
            Uri build = new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(getCustomAnimationId(Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_horizon_light_animation_style", 0, -2)))).build();
            this.mVideoPath = build;
            MediaPlayer create = MediaPlayer.create(this.mContext, build);
            this.mMediaPlayer = create;
            if (create == null || create.getDuration() <= 0) {
                setVisible(false);
            } else {
                setVisible(true);
                setLayoutResource(C0012R$layout.op_custom_notification_anim_choose_layout);
                this.mPreviewResource = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_preview, 0);
                this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPCustomNotificationAnimVideoPreference$XDIEVqYRxTMK4Qxo5ND3bUls_g */

                    public final void onSeekComplete(MediaPlayer mediaPlayer) {
                        OPCustomNotificationAnimVideoPreference.this.lambda$new$0$OPCustomNotificationAnimVideoPreference(mediaPlayer);
                    }
                });
                this.mMediaPlayer.setOnPreparedListener($$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM.INSTANCE);
                this.mAnimationAvailable = true;
                updateAspectRatio();
            }
        } catch (Exception unused) {
            Log.w("VideoPreference", "Animation resource not found. Will not show animation.");
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$OPCustomNotificationAnimVideoPreference(MediaPlayer mediaPlayer) {
        this.mVideoReady = true;
    }

    public boolean needShowWarningDialog() {
        for (int i = 0; i < this.mAnims.size(); i++) {
            if (this.mAnims.get(i).selected) {
                if (this.mLastIndex != this.mAnims.get(i).index) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setBackground(null);
        if (this.mAnimationAvailable) {
            ((ScrollView) preferenceViewHolder.findViewById(C0010R$id.video_container_scrollview)).setOnTouchListener(new View.OnTouchListener(this) {
                /* class com.oneplus.settings.ui.OPCustomNotificationAnimVideoPreference.AnonymousClass1 */

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            final ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_preview_image);
            final ImageView imageView2 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_play_button);
            imageView.setImageResource(this.mPreviewResource);
            ((AspectRatioFrameLayout) preferenceViewHolder.findViewById(C0010R$id.video_container)).setAspectRatio(this.mAspectRadio);
            ((TextureView) preferenceViewHolder.findViewById(C0010R$id.video_texture_view)).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                /* class com.oneplus.settings.ui.OPCustomNotificationAnimVideoPreference.AnonymousClass2 */

                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                }

                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    if (OPCustomNotificationAnimVideoPreference.this.mMediaPlayer != null) {
                        OPCustomNotificationAnimVideoPreference.this.mMediaPlayer.setSurface(new Surface(surfaceTexture));
                        OPCustomNotificationAnimVideoPreference.this.mVideoReady = false;
                        OPCustomNotificationAnimVideoPreference.this.mMediaPlayer.seekTo(0);
                    }
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    imageView.setVisibility(0);
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    if (OPCustomNotificationAnimVideoPreference.this.mVideoReady) {
                        if (imageView.getVisibility() == 0) {
                            imageView.setVisibility(8);
                        }
                        if (!OPCustomNotificationAnimVideoPreference.this.mVideoPaused && OPCustomNotificationAnimVideoPreference.this.mMediaPlayer != null && !OPCustomNotificationAnimVideoPreference.this.mMediaPlayer.isPlaying()) {
                            OPCustomNotificationAnimVideoPreference.this.mMediaPlayer.start();
                            imageView2.setVisibility(8);
                        }
                    }
                    if (OPCustomNotificationAnimVideoPreference.this.mMediaPlayer != null && !OPCustomNotificationAnimVideoPreference.this.mMediaPlayer.isPlaying()) {
                        imageView2.getVisibility();
                    }
                }
            });
            RecyclerView recyclerView = (RecyclerView) preferenceViewHolder.findViewById(C0010R$id.custom_fingerprint_anim_style_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
            linearLayoutManager.setOrientation(0);
            recyclerView.setLayoutManager(linearLayoutManager);
            initStyleAnimViews();
            recyclerView.addItemDecoration(new OPSpaceItemDecoration(this.mContext, this.mAnims.size(), (int) getContext().getResources().getDimension(C0007R$dimen.op_control_margin_space4)));
            recyclerView.setAdapter(new AnimStyleAdapter());
            int selectedAnimIndex = getSelectedAnimIndex();
            this.mSelectedIndex = selectedAnimIndex;
            if (selectedAnimIndex >= 0 && selectedAnimIndex < this.mAnims.size()) {
                linearLayoutManager.scrollToPosition(this.mSelectedIndex);
            }
            ((ImageView) preferenceViewHolder.findViewById(C0010R$id.anim_bg)).setBackgroundResource(C0008R$drawable.op_custom_aodpreview_none);
            preferenceViewHolder.findViewById(C0010R$id.bottom_icon_image).setVisibility(8);
            Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.save_button);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    /* class com.oneplus.settings.ui.OPCustomNotificationAnimVideoPreference.AnonymousClass3 */

                    public void onClick(View view) {
                        OPCustomNotificationAnimVideoPreference.this.saveSelectedAnim();
                        if (OPCustomNotificationAnimVideoPreference.this.mSettingsPreferenceFragment != null) {
                            OPCustomNotificationAnimVideoPreference.this.mSettingsPreferenceFragment.finish();
                        }
                    }
                });
            }
        }
    }

    private int getCustomAnimationId(int i) {
        if (i == 1) {
            return C0016R$raw.op_custom_horizon_light_red_anim;
        }
        if (i == 2) {
            return C0016R$raw.op_custom_horizon_light_gold_anim;
        }
        if (i == 3) {
            return C0016R$raw.op_custom_horizon_light_purple_anim;
        }
        if (i != 10) {
            return C0016R$raw.op_custom_horizon_light_blue_anim;
        }
        return C0016R$raw.op_custom_horizon_light_mcl_anim;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeAnimStyle(int i) {
        playAnimByStyle(i);
    }

    private void setAnimStyle(int i) {
        Log.d("VideoPreference", "setAnimStyle value:" + i);
        OPThemeUtils.enableTheme("oneplus_aodnotification", OPThemeUtils.getCurrentHorizonLightByIndex(this.mContext, i), this.mContext);
        OPThemeUtils.setCurrentHorizonLight(this.mContext, i);
        OPUtils.sendAppTrackerForHorizonLightAnimStyle();
    }

    private void playAnimByStyle(int i) {
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        }
        this.mVideoPath = new Uri.Builder().scheme("android.resource").authority(this.mContext.getPackageName()).appendPath(String.valueOf(getCustomAnimationId(i))).build();
        try {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.setDataSource(this.mContext, this.mVideoPath);
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
        }
        super.onDetached();
    }

    public void saveSelectedAnim() {
        setAnimStyle(this.mAnims.get(this.mSelectedIndex).index);
        int i = this.mAnims.get(this.mSelectedIndex).index;
        if (i == 0) {
            OPUtils.sendAnalytics("horizon", "status", "blue");
        } else if (i == 1) {
            OPUtils.sendAnalytics("horizon", "status", "red");
        } else if (i == 2) {
            OPUtils.sendAnalytics("horizon", "status", "gold");
        } else if (i == 3) {
            OPUtils.sendAnalytics("horizon", "status", "purple");
        }
    }

    private void updateAspectRatio() {
        this.mAspectRadio = ((float) this.mMediaPlayer.getVideoWidth()) / ((float) this.mMediaPlayer.getVideoHeight());
    }

    private void initStyleAnimViews() {
        this.mAnims.clear();
        OPCustomItemEntity oPCustomItemEntity = new OPCustomItemEntity(this.mContext.getString(C0017R$string.aod_horizon_light_effect_1), C0008R$drawable.op_custom_horizon_light_blue, 0);
        OPCustomItemEntity oPCustomItemEntity2 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.aod_horizon_light_effect_2), C0008R$drawable.op_custom_horizon_light_red, 1);
        OPCustomItemEntity oPCustomItemEntity3 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.aod_horizon_light_effect_4), C0008R$drawable.op_custom_horizon_light_gold, 2);
        OPCustomItemEntity oPCustomItemEntity4 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.aod_horizon_light_effect_3), C0008R$drawable.op_custom_horizon_light_purple, 3);
        OPCustomItemEntity oPCustomItemEntity5 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.aod_horizon_light_effect_5), C0008R$drawable.op_custom_horizon_light_mcl, 10);
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_horizon_light_animation_style", 0, -2);
        Log.v("OPCustomNotificationAnimVideoPreference", "initStyleAnimViews  AOD style = " + intForUser);
        if (intForUser == 0) {
            oPCustomItemEntity.selected = true;
        } else if (intForUser == 1) {
            oPCustomItemEntity2.selected = true;
        } else if (intForUser == 2) {
            oPCustomItemEntity3.selected = true;
        } else if (intForUser == 3) {
            oPCustomItemEntity4.selected = true;
        } else if (intForUser != 10) {
            oPCustomItemEntity.selected = true;
        } else {
            oPCustomItemEntity5.selected = true;
        }
        if (OPThemeUtils.isSupportMclTheme()) {
            this.mAnims.add(oPCustomItemEntity5);
        }
        this.mAnims.add(oPCustomItemEntity);
        this.mAnims.add(oPCustomItemEntity2);
        this.mAnims.add(oPCustomItemEntity3);
        this.mAnims.add(oPCustomItemEntity4);
    }

    class AnimStyleAdapter extends RecyclerView.Adapter<OPCustomItemEntityViewHolder> {
        AnimStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public OPCustomItemEntityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new OPCustomItemEntityViewHolder(LayoutInflater.from(OPCustomNotificationAnimVideoPreference.this.mContext).inflate(C0012R$layout.op_custom_clock_choose_item, (ViewGroup) null));
        }

        public void onBindViewHolder(OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, int i) {
            OPCustomItemEntity oPCustomItemEntity = (OPCustomItemEntity) OPCustomNotificationAnimVideoPreference.this.mAnims.get(i);
            oPCustomItemEntityViewHolder.imageView.setImageResource(oPCustomItemEntity.resId);
            oPCustomItemEntityViewHolder.textView.setText(oPCustomItemEntity.name);
            if (oPCustomItemEntity.selected) {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
                oPCustomItemEntityViewHolder.imageView.setSelected(true);
                oPCustomItemEntityViewHolder.textView.setSelected(true);
                OPCustomNotificationAnimVideoPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            } else {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(4);
                oPCustomItemEntityViewHolder.imageView.setSelected(false);
                oPCustomItemEntityViewHolder.textView.setSelected(false);
            }
            oPCustomItemEntityViewHolder.itemView.setOnClickListener(new View.OnClickListener(i, oPCustomItemEntityViewHolder, oPCustomItemEntity) {
                /* class com.oneplus.settings.ui.$$Lambda$OPCustomNotificationAnimVideoPreference$AnimStyleAdapter$cr511MMFNCSomdwqRteceQ4v0Ww */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPCustomItemEntityViewHolder f$2;
                public final /* synthetic */ OPCustomItemEntity f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    OPCustomNotificationAnimVideoPreference.AnimStyleAdapter.this.lambda$onBindViewHolder$0$OPCustomNotificationAnimVideoPreference$AnimStyleAdapter(this.f$1, this.f$2, this.f$3, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPCustomNotificationAnimVideoPreference$AnimStyleAdapter(int i, OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, OPCustomItemEntity oPCustomItemEntity, View view) {
            OPCustomNotificationAnimVideoPreference.this.setSelectedAnim(i);
            if (OPCustomNotificationAnimVideoPreference.this.mCurrentVH != null) {
                if (OPCustomNotificationAnimVideoPreference.this.mCurrentVH.imageViewMask != null) {
                    OPCustomNotificationAnimVideoPreference.this.mCurrentVH.imageViewMask.setVisibility(4);
                }
                if (OPCustomNotificationAnimVideoPreference.this.mCurrentVH.imageView != null) {
                    OPCustomNotificationAnimVideoPreference.this.mCurrentVH.imageView.setSelected(false);
                }
                if (OPCustomNotificationAnimVideoPreference.this.mCurrentVH.textView != null) {
                    OPCustomNotificationAnimVideoPreference.this.mCurrentVH.textView.setSelected(false);
                }
            }
            oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
            oPCustomItemEntityViewHolder.imageView.setSelected(true);
            oPCustomItemEntityViewHolder.textView.setSelected(true);
            OPCustomNotificationAnimVideoPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            OPCustomNotificationAnimVideoPreference.this.changeAnimStyle(oPCustomItemEntity.index);
            OPCustomNotificationAnimVideoPreference.this.mSelectedIndex = i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPCustomNotificationAnimVideoPreference.this.mAnims.size();
        }
    }

    private int getSelectedAnimIndex() {
        for (int i = 0; i < this.mAnims.size(); i++) {
            if (this.mAnims.get(i).selected) {
                return i;
            }
        }
        return 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setSelectedAnim(int i) {
        for (int i2 = 0; i2 < this.mAnims.size(); i2++) {
            if (i == i2) {
                this.mAnims.get(i2).selected = true;
            } else {
                this.mAnims.get(i2).selected = false;
            }
        }
    }
}

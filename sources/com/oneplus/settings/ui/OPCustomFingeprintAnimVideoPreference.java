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
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.ui.OPCustomFingeprintAnimVideoPreference;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OPCustomFingeprintAnimVideoPreference extends Preference {
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

    public OPCustomFingeprintAnimVideoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mLastIndex = Settings.System.getIntForUser(context.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        try {
            obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_animation, 0);
            Uri build = new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(getCustomAnimationId(Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", 0, -2)))).build();
            this.mVideoPath = build;
            MediaPlayer create = MediaPlayer.create(this.mContext, build);
            this.mMediaPlayer = create;
            if (create == null || create.getDuration() <= 0) {
                setVisible(false);
            } else {
                setVisible(true);
                int i = C0012R$layout.op_custom_fingerprint_anim_choose_layout;
                OpCustomizeSettings.CUSTOM_TYPE.MCL.equals(OpCustomizeSettings.getCustomType());
                setLayoutResource(i);
                this.mPreviewResource = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_preview, 0);
                this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPCustomFingeprintAnimVideoPreference$gv59WWVB_M8C60fnrT6plLBUE */

                    public final void onSeekComplete(MediaPlayer mediaPlayer) {
                        OPCustomFingeprintAnimVideoPreference.this.lambda$new$0$OPCustomFingeprintAnimVideoPreference(mediaPlayer);
                    }
                });
                this.mMediaPlayer.setOnPreparedListener($$Lambda$OPCustomFingeprintAnimVideoPreference$qmtCZ1Cotcht_SOWUlUe6wB8W8.INSTANCE);
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
    public /* synthetic */ void lambda$new$0$OPCustomFingeprintAnimVideoPreference(MediaPlayer mediaPlayer) {
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
            ((ScrollView) preferenceViewHolder.findViewById(C0010R$id.video_container_scrollview)).setOnTouchListener($$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw.INSTANCE);
            final ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_preview_image);
            final ImageView imageView2 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_play_button);
            imageView.setImageResource(this.mPreviewResource);
            ((AspectRatioFrameLayout) preferenceViewHolder.findViewById(C0010R$id.video_container)).setAspectRatio(this.mAspectRadio);
            ((TextureView) preferenceViewHolder.findViewById(C0010R$id.video_texture_view)).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                /* class com.oneplus.settings.ui.OPCustomFingeprintAnimVideoPreference.AnonymousClass1 */

                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                }

                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    if (OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer != null) {
                        OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer.setSurface(new Surface(surfaceTexture));
                        OPCustomFingeprintAnimVideoPreference.this.mVideoReady = false;
                        OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer.seekTo(0);
                    }
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    imageView.setVisibility(0);
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    if (OPCustomFingeprintAnimVideoPreference.this.mVideoReady) {
                        if (imageView.getVisibility() == 0) {
                            imageView.setVisibility(8);
                        }
                        if (!OPCustomFingeprintAnimVideoPreference.this.mVideoPaused && OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer != null && !OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer.isPlaying()) {
                            OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer.start();
                            imageView2.setVisibility(8);
                        }
                    }
                    if (OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer != null && !OPCustomFingeprintAnimVideoPreference.this.mMediaPlayer.isPlaying()) {
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
            Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.save_button);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    /* class com.oneplus.settings.ui.OPCustomFingeprintAnimVideoPreference.AnonymousClass2 */

                    public void onClick(View view) {
                        OPCustomFingeprintAnimVideoPreference.this.saveSelectedAnim();
                        if (OPCustomFingeprintAnimVideoPreference.this.mSettingsPreferenceFragment != null) {
                            OPCustomFingeprintAnimVideoPreference.this.mSettingsPreferenceFragment.finish();
                        }
                    }
                });
            }
        }
    }

    private int getCustomAnimationId(int i) {
        if (i == 1) {
            return C0016R$raw.op_custom_fingerprint_anim_2;
        }
        if (i == 2) {
            return C0016R$raw.op_custom_fingerprint_anim_3;
        }
        if (i == 3) {
            return C0016R$raw.op_custom_fingerprint_anim_5;
        }
        if (i == 4) {
            return C0016R$raw.op_custom_fingerprint_anim_6;
        }
        if (i != 9) {
            return C0016R$raw.op_custom_fingerprint_anim_1;
        }
        return C0016R$raw.op_custom_fingerprint_anim_4;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeAnimStyle(int i) {
        playAnimByStyle(i);
    }

    private void setAnimStyle(int i) {
        Log.d("VideoPreference", "setAnimStyle value:" + i);
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", i, -2);
        OPUtils.sendAppTrackerForFodAnimStyle();
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
    }

    /* access modifiers changed from: package-private */
    public void updateAspectRatio() {
        this.mAspectRadio = ((float) this.mMediaPlayer.getVideoWidth()) / ((float) this.mMediaPlayer.getVideoHeight());
    }

    private void initStyleAnimViews() {
        this.mAnims.clear();
        OPCustomItemEntity oPCustomItemEntity = null;
        OPCustomItemEntity oPCustomItemEntity2 = OPThemeUtils.isSupportMclTheme() ? new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_mcl), C0008R$drawable.op_img_fod_05, 3) : null;
        OPCustomItemEntity oPCustomItemEntity3 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_1), C0008R$drawable.op_img_fod_01, 0);
        OPCustomItemEntity oPCustomItemEntity4 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_4), C0008R$drawable.op_img_fod_02, 1);
        OPCustomItemEntity oPCustomItemEntity5 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_3), C0008R$drawable.op_img_fod_03, 2);
        OPCustomItemEntity oPCustomItemEntity6 = new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_none), C0008R$drawable.op_img_fod_04, 9);
        if (OPUtils.isSM8250Products()) {
            oPCustomItemEntity = new OPCustomItemEntity(this.mContext.getString(C0017R$string.oneplus_select_fingerprint_animation_effect_6), C0008R$drawable.op_img_fod_06, 4);
        }
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        Log.v("OPCustomFingerAnimVideoPreference", "initStyleAnimViews  FOD style = " + intForUser);
        if (intForUser == 1) {
            oPCustomItemEntity4.selected = true;
        } else if (intForUser == 2) {
            oPCustomItemEntity5.selected = true;
        } else if (intForUser != 3) {
            if (intForUser != 4) {
                if (intForUser != 9) {
                    oPCustomItemEntity3.selected = true;
                } else {
                    oPCustomItemEntity6.selected = true;
                }
            } else if (oPCustomItemEntity != null) {
                oPCustomItemEntity.selected = true;
            }
        } else if (oPCustomItemEntity2 != null) {
            oPCustomItemEntity2.selected = true;
        }
        if (oPCustomItemEntity != null) {
            this.mAnims.add(oPCustomItemEntity);
        }
        if (OPThemeUtils.isSupportMclTheme()) {
            this.mAnims.add(oPCustomItemEntity2);
        }
        this.mAnims.add(oPCustomItemEntity3);
        this.mAnims.add(oPCustomItemEntity4);
        this.mAnims.add(oPCustomItemEntity5);
        this.mAnims.add(oPCustomItemEntity6);
    }

    class AnimStyleAdapter extends RecyclerView.Adapter<OPCustomItemEntityViewHolder> {
        AnimStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public OPCustomItemEntityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new OPCustomItemEntityViewHolder(LayoutInflater.from(OPCustomFingeprintAnimVideoPreference.this.mContext).inflate(C0012R$layout.op_custom_clock_choose_item, (ViewGroup) null));
        }

        public void onBindViewHolder(OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, int i) {
            OPCustomItemEntity oPCustomItemEntity = (OPCustomItemEntity) OPCustomFingeprintAnimVideoPreference.this.mAnims.get(i);
            oPCustomItemEntityViewHolder.imageView.setImageResource(oPCustomItemEntity.resId);
            oPCustomItemEntityViewHolder.textView.setText(oPCustomItemEntity.name);
            oPCustomItemEntityViewHolder.itemView.setOnClickListener(new View.OnClickListener(i, oPCustomItemEntityViewHolder, oPCustomItemEntity) {
                /* class com.oneplus.settings.ui.$$Lambda$OPCustomFingeprintAnimVideoPreference$AnimStyleAdapter$yv27oEByMWs9Gyypj8BhtL7PFdI */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPCustomItemEntityViewHolder f$2;
                public final /* synthetic */ OPCustomItemEntity f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    OPCustomFingeprintAnimVideoPreference.AnimStyleAdapter.this.lambda$onBindViewHolder$0$OPCustomFingeprintAnimVideoPreference$AnimStyleAdapter(this.f$1, this.f$2, this.f$3, view);
                }
            });
            if (oPCustomItemEntity.selected) {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
                oPCustomItemEntityViewHolder.imageView.setSelected(true);
                oPCustomItemEntityViewHolder.textView.setSelected(true);
                OPCustomFingeprintAnimVideoPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
                return;
            }
            oPCustomItemEntityViewHolder.imageViewMask.setVisibility(4);
            oPCustomItemEntityViewHolder.imageView.setSelected(false);
            oPCustomItemEntityViewHolder.textView.setSelected(false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPCustomFingeprintAnimVideoPreference$AnimStyleAdapter(int i, OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, OPCustomItemEntity oPCustomItemEntity, View view) {
            OPCustomFingeprintAnimVideoPreference.this.setSelectedAnim(i);
            if (OPCustomFingeprintAnimVideoPreference.this.mCurrentVH != null) {
                if (OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.imageViewMask != null) {
                    OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.imageViewMask.setVisibility(4);
                }
                if (OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.imageView != null) {
                    OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.imageView.setSelected(false);
                }
                if (OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.textView != null) {
                    OPCustomFingeprintAnimVideoPreference.this.mCurrentVH.textView.setSelected(false);
                }
            }
            oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
            oPCustomItemEntityViewHolder.imageView.setSelected(true);
            oPCustomItemEntityViewHolder.textView.setSelected(true);
            OPCustomFingeprintAnimVideoPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            OPCustomFingeprintAnimVideoPreference.this.changeAnimStyle(oPCustomItemEntity.index);
            OPCustomFingeprintAnimVideoPreference.this.mSelectedIndex = i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPCustomFingeprintAnimVideoPreference.this.mAnims.size();
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

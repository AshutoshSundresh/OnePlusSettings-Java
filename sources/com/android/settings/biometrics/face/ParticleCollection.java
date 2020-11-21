package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.android.settings.C0006R$color;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.face.AnimationParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleCollection implements BiometricEnrollSidecar.Listener {
    private Listener mListener;
    private final List<AnimationParticle> mParticleList = new ArrayList();
    private final AnimationParticle.Listener mParticleListener = new AnimationParticle.Listener() {
        /* class com.android.settings.biometrics.face.ParticleCollection.AnonymousClass1 */

        @Override // com.android.settings.biometrics.face.AnimationParticle.Listener
        public void onRingCompleted(int i) {
            boolean isEmpty = ParticleCollection.this.mPrimariesInProgress.isEmpty();
            int i2 = 0;
            while (true) {
                if (i2 >= ParticleCollection.this.mPrimariesInProgress.size()) {
                    break;
                } else if (((Integer) ParticleCollection.this.mPrimariesInProgress.get(i2)).intValue() == i) {
                    ParticleCollection.this.mPrimariesInProgress.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            if (ParticleCollection.this.mPrimariesInProgress.isEmpty() && !isEmpty) {
                ParticleCollection.this.mListener.onEnrolled();
            }
        }
    };
    private final List<Integer> mPrimariesInProgress;
    private int mState;

    public interface Listener {
        void onEnrolled();
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
    }

    public ParticleCollection(Context context, Listener listener, Rect rect, int i) {
        this.mListener = listener;
        ArrayList arrayList = new ArrayList();
        Resources.Theme theme = context.getTheme();
        Resources resources = context.getResources();
        arrayList.add(Integer.valueOf(resources.getColor(C0006R$color.face_anim_particle_color_1, theme)));
        arrayList.add(Integer.valueOf(resources.getColor(C0006R$color.face_anim_particle_color_2, theme)));
        arrayList.add(Integer.valueOf(resources.getColor(C0006R$color.face_anim_particle_color_3, theme)));
        arrayList.add(Integer.valueOf(resources.getColor(C0006R$color.face_anim_particle_color_4, theme)));
        this.mPrimariesInProgress = new ArrayList(Arrays.asList(0, 4, 8));
        int[] iArr = {3, 7, 11, 2, 6, 10, 1, 5, 9, 0, 4, 8};
        for (int i2 = 0; i2 < 12; i2++) {
            AnimationParticle animationParticle = new AnimationParticle(context, this.mParticleListener, rect, i, iArr[i2], 12, arrayList);
            if (this.mPrimariesInProgress.contains(Integer.valueOf(iArr[i2]))) {
                animationParticle.setAsPrimary();
            }
            this.mParticleList.add(animationParticle);
        }
        updateState(1);
    }

    public void update(long j, long j2) {
        for (int i = 0; i < this.mParticleList.size(); i++) {
            this.mParticleList.get(i).update(j, j2);
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < this.mParticleList.size(); i++) {
            this.mParticleList.get(i).draw(canvas);
        }
    }

    private void updateState(int i) {
        if (this.mState != i) {
            for (int i2 = 0; i2 < this.mParticleList.size(); i2++) {
                this.mParticleList.get(i2).updateState(i);
            }
            this.mState = i;
        }
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        if (i2 == 0) {
            updateState(4);
        }
    }
}

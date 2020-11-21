package androidx.leanback.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.ArrayList;

public final class TransitionHelper {
    public static boolean systemSupportsEntranceTransitions() {
        return Build.VERSION.SDK_INT >= 21;
    }

    private static class TransitionStub {
        ArrayList<TransitionListener> mTransitionListeners;

        TransitionStub() {
        }
    }

    public static void setSharedElementEnterTransition(Window window, Object obj) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setSharedElementEnterTransition((Transition) obj);
        }
    }

    public static Object getSharedElementReturnTransition(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            return window.getSharedElementReturnTransition();
        }
        return null;
    }

    public static void setSharedElementReturnTransition(Window window, Object obj) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setSharedElementReturnTransition((Transition) obj);
        }
    }

    public static Object getEnterTransition(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            return window.getEnterTransition();
        }
        return null;
    }

    public static void setEnterTransition(Window window, Object obj) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setEnterTransition((Transition) obj);
        }
    }

    public static Object getReturnTransition(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            return window.getReturnTransition();
        }
        return null;
    }

    public static void setReturnTransition(Window window, Object obj) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setReturnTransition((Transition) obj);
        }
    }

    public static Object createScene(ViewGroup viewGroup, Runnable runnable) {
        if (Build.VERSION.SDK_INT < 19) {
            return runnable;
        }
        Scene scene = new Scene(viewGroup);
        scene.setEnterAction(runnable);
        return scene;
    }

    public static Object createChangeBounds(boolean z) {
        if (Build.VERSION.SDK_INT < 19) {
            return new TransitionStub();
        }
        CustomChangeBounds customChangeBounds = new CustomChangeBounds();
        customChangeBounds.setReparent(z);
        return customChangeBounds;
    }

    public static Object createChangeTransform() {
        if (Build.VERSION.SDK_INT >= 21) {
            return new ChangeTransform();
        }
        return new TransitionStub();
    }

    public static Object createTransitionSet(boolean z) {
        if (Build.VERSION.SDK_INT < 19) {
            return new TransitionStub();
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.setOrdering(z ? 1 : 0);
        return transitionSet;
    }

    public static void addTransition(Object obj, Object obj2) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((TransitionSet) obj).addTransition((Transition) obj2);
        }
    }

    public static void exclude(Object obj, int i, boolean z) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((Transition) obj).excludeTarget(i, z);
        }
    }

    public static void exclude(Object obj, View view, boolean z) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((Transition) obj).excludeTarget(view, z);
        }
    }

    public static void include(Object obj, int i) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((Transition) obj).addTarget(i);
        }
    }

    public static void include(Object obj, View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((Transition) obj).addTarget(view);
        }
    }

    public static void setStartDelay(Object obj, long j) {
        if (Build.VERSION.SDK_INT >= 19) {
            ((Transition) obj).setStartDelay(j);
        }
    }

    public static Object createFadeTransition(int i) {
        if (Build.VERSION.SDK_INT >= 19) {
            return new Fade(i);
        }
        return new TransitionStub();
    }

    public static void addTransitionListener(Object obj, final TransitionListener transitionListener) {
        if (transitionListener != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                AnonymousClass1 r0 = new Transition.TransitionListener() {
                    /* class androidx.leanback.transition.TransitionHelper.AnonymousClass1 */

                    public void onTransitionStart(Transition transition) {
                        TransitionListener.this.onTransitionStart(transition);
                    }

                    public void onTransitionResume(Transition transition) {
                        TransitionListener.this.onTransitionResume(transition);
                    }

                    public void onTransitionPause(Transition transition) {
                        TransitionListener.this.onTransitionPause(transition);
                    }

                    public void onTransitionEnd(Transition transition) {
                        TransitionListener.this.onTransitionEnd(transition);
                    }

                    public void onTransitionCancel(Transition transition) {
                        TransitionListener.this.onTransitionCancel(transition);
                    }
                };
                transitionListener.mImpl = r0;
                ((Transition) obj).addListener(r0);
                return;
            }
            TransitionStub transitionStub = (TransitionStub) obj;
            if (transitionStub.mTransitionListeners == null) {
                transitionStub.mTransitionListeners = new ArrayList<>();
            }
            transitionStub.mTransitionListeners.add(transitionListener);
        }
    }

    public static void runTransition(Object obj, Object obj2) {
        ArrayList<TransitionListener> arrayList;
        ArrayList<TransitionListener> arrayList2;
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionManager.go((Scene) obj, (Transition) obj2);
            return;
        }
        TransitionStub transitionStub = (TransitionStub) obj2;
        if (!(transitionStub == null || (arrayList2 = transitionStub.mTransitionListeners) == null)) {
            int size = arrayList2.size();
            for (int i = 0; i < size; i++) {
                transitionStub.mTransitionListeners.get(i).onTransitionStart(obj2);
            }
        }
        Runnable runnable = (Runnable) obj;
        if (runnable != null) {
            runnable.run();
        }
        if (!(transitionStub == null || (arrayList = transitionStub.mTransitionListeners) == null)) {
            int size2 = arrayList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                transitionStub.mTransitionListeners.get(i2).onTransitionEnd(obj2);
            }
        }
    }

    public static Object loadTransition(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 19) {
            return TransitionInflater.from(context).inflateTransition(i);
        }
        return new TransitionStub();
    }

    public static Object createFadeAndShortSlide(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new FadeAndShortSlide(i);
        }
        return new TransitionStub();
    }

    public static Object createFadeAndShortSlide(int i, float f) {
        if (Build.VERSION.SDK_INT < 21) {
            return new TransitionStub();
        }
        FadeAndShortSlide fadeAndShortSlide = new FadeAndShortSlide(i);
        fadeAndShortSlide.setDistance(f);
        return fadeAndShortSlide;
    }

    public static void beginDelayedTransition(ViewGroup viewGroup, Object obj) {
        if (Build.VERSION.SDK_INT >= 21) {
            TransitionManager.beginDelayedTransition(viewGroup, (Transition) obj);
        }
    }

    public static void setEpicenterCallback(Object obj, final TransitionEpicenterCallback transitionEpicenterCallback) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (transitionEpicenterCallback == null) {
            ((Transition) obj).setEpicenterCallback(null);
        } else {
            ((Transition) obj).setEpicenterCallback(new Transition.EpicenterCallback() {
                /* class androidx.leanback.transition.TransitionHelper.AnonymousClass2 */

                public Rect onGetEpicenter(Transition transition) {
                    return TransitionEpicenterCallback.this.onGetEpicenter(transition);
                }
            });
        }
    }
}

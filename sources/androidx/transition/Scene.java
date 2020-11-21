package androidx.transition;

import android.view.ViewGroup;

public class Scene {
    private Runnable mExitAction;
    private ViewGroup mSceneRoot;

    public void exit() {
        Runnable runnable;
        if (getCurrentScene(this.mSceneRoot) == this && (runnable = this.mExitAction) != null) {
            runnable.run();
        }
    }

    static void setCurrentScene(ViewGroup viewGroup, Scene scene) {
        viewGroup.setTag(R$id.transition_current_scene, scene);
    }

    public static Scene getCurrentScene(ViewGroup viewGroup) {
        return (Scene) viewGroup.getTag(R$id.transition_current_scene);
    }
}

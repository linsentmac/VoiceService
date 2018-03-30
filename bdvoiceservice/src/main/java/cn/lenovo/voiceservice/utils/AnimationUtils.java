package cn.lenovo.voiceservice.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import static java.lang.Thread.sleep;

/**
 * Created by tmac on 17-11-23.
 */

public class AnimationUtils {

    public static Animation getAnimation(){
        AnimationSet animationSet=new AnimationSet(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(1, 1.3f, 1, 1.3f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);
        return animationSet;
    }

    public static void playAnimation(final View view, final AnimationListener listener){
        AnimationSet animationSet=new AnimationSet(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(1, 1.3f, 1, 1.3f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);
        view.startAnimation(animationSet);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(250);
                    listener.EndAnimation(view);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface AnimationListener{
        void EndAnimation(View view);
    }

}

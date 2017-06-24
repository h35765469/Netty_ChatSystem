package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by User on 2016/10/18.
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF mPointF1;
    private PointF mPointF2;

    public BezierEvaluator(PointF pointF1, PointF pointF2) {
        this.mPointF1 = pointF1;
        this.mPointF2 = pointF2;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float time = 1 - fraction;
        PointF result = new PointF();// 结果

        result.x = time * time * time * startValue.x + 3 * mPointF1.x * time
                * time * fraction + 3 * mPointF2.x * fraction * fraction * time
                + endValue.x * fraction * fraction * fraction;

        result.y = time * time * time * startValue.y + 3 * mPointF1.y * time
                * time * fraction + 3 * mPointF2.y * fraction * fraction * time
                + endValue.y * fraction * fraction * fraction;

        return result;
    }
}
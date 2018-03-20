package com.emadoki.edstackycard;

import android.view.View;

public class CardTransformer
{
    private EdStackyCard.Config config;

    public CardTransformer(EdStackyCard.Config config)
    {
        this.config = config;
    }

    public void transform(View view, float progress, int fromPosition, int toPosition)
    {
        float factor = 0.2f / config.displayAmount;
        float fromScale = fromPosition * factor;
        float toScale = toPosition * factor;
        float scale = fromScale + ((toScale - fromScale) * progress);
        view.setPivotY(0);
        view.setScaleX(0.8f + scale);
        view.setScaleY(0.8f + scale);

        view.setTranslationY(view.getMeasuredHeight() / config.displayAmount * scale);
    }

    public void transformAdd(View view, float progress, int position)
    {
        float factor = 0.2f / config.displayAmount;
        float scale = position * factor;
        view.setPivotX(view.getMeasuredWidth() / 2f);
        view.setPivotY(0);
        view.setScaleX(0.8f + scale);
        view.setScaleY(0.8f + scale);

        float y = view.getMeasuredHeight() / config.displayAmount * scale;
        float dy = view.getMeasuredHeight() * (1f - progress);
        view.setTranslationY(y - dy);
        view.setTranslationX(0);
    }
}

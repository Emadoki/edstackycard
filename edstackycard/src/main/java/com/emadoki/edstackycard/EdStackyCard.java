package com.emadoki.edstackycard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EdStackyCard extends ViewGroup
{
    private Config config;
    private CardTransformer cardTransformer;
    private OnInteractListener interactListener;
    private ArrayAdapter<?> arrayAdapter;
    private Vector2D touch;
    private Vector2D drag;

    public EdStackyCard(Context context)
    {
        super(context);
        initialize(null);
    }

    public EdStackyCard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(attrs);
    }

    public EdStackyCard(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY)
        {
            int childCount = getChildCount();
            int childWidth = 0, childHeight = 0;
            for (int i = 0; i < childCount; i++)
            {
                View childView = getChildAt(i);
                childWidth = Math.max(childView.getMeasuredWidth(), childWidth);
                childHeight = Math.max(childView.getMeasuredHeight(), childHeight);
            }
            setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize : childWidth,
                    (heightMode == MeasureSpec.EXACTLY) ? heightSize : childHeight);
        }
        else
        {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int top, int left, int right, int bottom)
    {
        animation(true, 1f);
    }

    private void initialize(AttributeSet attrs)
    {
        config = new Config();
        config.displayAmount = 5;
        config.animationDuration = 250;
        config.isAnimating = false;
        config.isInfiniteLoop = true;

        if (attrs != null)
        {
            TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.EdStackyCard, 0, 0);
            config.displayAmount = ta.getInteger(R.styleable.EdStackyCard_esc_displayAmount, config.displayAmount);
            config.animationDuration = ta.getInteger(R.styleable.EdStackyCard_esc_animationDuration, config.animationDuration);
            config.isInfiniteLoop = ta.getBoolean(R.styleable.EdStackyCard_esc_infiniteLoop, config.isInfiniteLoop);
        }

        cardTransformer = new CardTransformer(config);
        touch = new Vector2D();
        drag = new Vector2D();
    }

    private void load()
    {
        removeAllViews();
        int count = Math.min(arrayAdapter.getCount(), config.displayAmount);
        for (int i = 0; i < count; i++)
        {
            addCard(config.index % arrayAdapter.getCount(), i);
            config.index++;
        }
        for (int i = getChildCount() - 1; i >= 0; i--)
            getChildAt(i).bringToFront();
    }

    /**
     * Add a card using the view in arrayadapter at a certain position in the stack
     * @param position position in arrayadapter
     * @param positionInView position in the stack 0 = behind
     */
    private void addCard(int position, int positionInView)
    {
        addView(getCardView(position), positionInView);
        getChildAt(positionInView).setVisibility(View.INVISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(config.animationDuration);
        animator.setStartDelay(80 * (positionInView + 1));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            private int pos;
            private ValueAnimator.AnimatorUpdateListener apply(int pos)
            {
                this.pos = pos;
                return this;
            }
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (getChildAt(pos) != null)
                {
                    getChildAt(pos).setVisibility(View.VISIBLE);
                    config.isAnimating = true;
                    float progress = (float) valueAnimator.getAnimatedValue();
                    cardTransformer.transformAdd(getChildAt(pos), progress, pos);
                }
            }
        }.apply(positionInView));
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                config.isAnimating = false;
            }
        });
        animator.start();
    }

    /**
     * Remove card of certain position in the stack
     * @param position of the card in stack
     */
    private void removeCard(int position)
    {
        removeViewAt(position);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(config.animationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                config.isAnimating = true;
                float progress = (float) valueAnimator.getAnimatedValue();
                animation(false, progress);
            }
        });
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                config.isAnimating = false;
                if (!config.isInfiniteLoop)
                {
                    if (config.index >= arrayAdapter.getCount())
                    {
                        if (getChildCount() == 0)
                            config.index = 0;
                        return;
                    }
                }
                addCard(config.index % arrayAdapter.getCount(), 0);
                config.index++;
            }
        });
        animator.start();
    }

    private void animation(boolean isPreSet, float progress)
    {
        if (getChildCount() < config.displayAmount)
        {
            int pax = config.displayAmount - getChildCount() - 1;
            for (int i = getChildCount() - 1; i >= 0; i--)
            {
                View child = getChildAt(i);
                if (isPreSet)
                {
                    preSet(child);
                    cardTransformer.transform(child, progress, pax + i - 1, pax + i);
                }
                else
                {
                    cardTransformer.transform(child, progress, pax + i, pax + i + 1);
                }
            }
        }
        else
        {
            for (int i = getChildCount() - 1; i >= 0; i--)
            {
                View child = getChildAt(i);
                if (isPreSet)
                {
                    preSet(child);
                    cardTransformer.transform(child, progress, i - 1, i);
                }
                else
                {
                    cardTransformer.transform(child, progress, i, i + 1);
                }
            }
        }
    }

    private void preSet(View view)
    {
        //child.layout(top, left, right, bottom);
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + view.getMeasuredWidth();
        int childBottom = childTop + view.getMeasuredHeight();
        view.layout(childLeft, childTop, childRight, childBottom);
    }

    /**
     * Return the view of the child in certain position
     * @param position the child position
     * @return
     */
    private View getCardView(int position)
    {
        View child = arrayAdapter.getView(position, null, this);
        child.setTag(position);
        return child;
    }

    /**
     * Register a listener to receive events callbacks
     * @param listener
     */
    public void setOnInteractListener(OnInteractListener listener)
    {
        this.interactListener = listener;
    }

    /**
     * Set a new adapter to provide child views on demand.
     * @param adapter
     */
    public void setAdapter(ArrayAdapter<?> adapter)
    {
        arrayAdapter = adapter;
        arrayAdapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
                config.index = calculateTopIndex();
                load();
            }
        });
        config.index = 0;
        load();
    }

    /**
     * Return the registered adapter
     * @return
     */
    public ArrayAdapter<?> getAdapter()
    {
        return arrayAdapter;
    }

    /**
     * Get the array position of the top card in the stack
     * @return position
     */
    private int calculateTopIndex()
    {
        int index = config.index - config.displayAmount;
        if (index < 0)
            index = 0;
        else if (arrayAdapter.getCount() > 0)
            index %= arrayAdapter.getCount();

        return index;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        View child = getChildAt(getChildCount() - 1);

        if (child == null)
            return super.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // initial position of pointer
                touch.x = event.getX() - child.getTranslationX();
                touch.y = event.getY() - child.getTranslationY();
                // reset drag amount
                drag.x = 0;
                drag.y = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                drag.x = event.getX() - touch.x;
                drag.y = event.getY() - touch.y;
                child.setTranslationX(drag.x);
                child.setTranslationY(drag.y);
                break;
            case MotionEvent.ACTION_UP:
                float radius = Math.min(getWidth(), getHeight());
                radius *= 0.5f;
                if (drag.length() > radius)
                {
                    removeCard(getChildCount() - 1);
                    // notify user
                    if (interactListener != null)
                        interactListener.dismiss(child, (Integer) child.getTag());
                }
                else
                {
                    float y = child.getMeasuredHeight() / config.displayAmount * 0.2f;
                    child.animate().translationX(0).translationY(y).setDuration(config.animationDuration).start();
                }
                break;
        }

        return true;
    }

    /**
     * Just a class to hold the variables and to be pass around
     */
    public static class Config
    {
        public int index;
        public int displayAmount;
        public int animationDuration;
        public boolean isAnimating;
        public boolean isInfiniteLoop;
    }

    public interface OnInteractListener
    {
        void dismiss(View view, int position);
    }
}

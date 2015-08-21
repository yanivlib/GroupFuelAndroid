package com.mty.groupfuel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoCarsView extends RelativeLayout {

    private Button button;
    private Context context;
    private TextView textView;

    public NoCarsView(Context context) {
        this(context, null);
    }

    public NoCarsView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public NoCarsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.no_cars_item_children, this, true);
        this.context = context;
        setupChildren();
    }

    public static NoCarsView inflate(ViewGroup parent) {
        NoCarsView noCarsView = (NoCarsView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.no_cars_item, parent, false);
        return noCarsView;
    }

    private void setupChildren() {
        this.button = (Button) findViewById(R.id.button);
        this.textView = (TextView) findViewById(R.id.title);
        button.setOnClickListener(new UsageFragment.AddCarListener(context));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setButtonText(CharSequence text) {
        this.button.setText(text);
    }

    public void setButtonListener(OnClickListener onClickListener) {
        this.button.setOnClickListener(onClickListener);
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        this.button.setEnabled(buttonEnabled);
    }

    public void setButtonVisibility(int buttonVisibility) {
        this.button.setVisibility(buttonVisibility);
    }
}

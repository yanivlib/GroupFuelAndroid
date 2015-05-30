package com.mty.groupfuel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

public class HintAdapter extends ArrayAdapter<String> {
    private static final String prompt = "Please select";

    public HintAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void clear() {
        super.clear();
        add(prompt);
    }

    @Override
    public void addAll(Collection<? extends String> collection) {
        remove(prompt);
        super.addAll(collection);
        add(prompt);
    }
}
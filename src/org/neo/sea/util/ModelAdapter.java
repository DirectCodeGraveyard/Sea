package org.neo.sea.util;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class ModelAdapter<T> extends BaseAdapter {
    private final Context context;
    private List<T> items;

    public ModelAdapter(List<T> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int index) {
        return items.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    public void updateItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}

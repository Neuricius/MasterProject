package com.neuricius.masterproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.model.NavigationItem;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter {

    // A reference to the context (i.e. the activity containing the adapter)
    Context context;
    ArrayList<NavigationItem> items;

    // Constructor should at least have context as a parameter
    public DrawerListAdapter(Context context, ArrayList<NavigationItem> items) {

        this.context = context;
        this.items = items;
    }

    // Returns the item count
    @Override
    public int getCount() {
        return items.size();
    }

    // Returns an item
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    // Returns an item ID
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Returns a view that corresponds with an item
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }

        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvSubtitle = view.findViewById(R.id.subtitle);
        ImageView ivIcon = view.findViewById(R.id.icon);

        tvTitle.setText(items.get(position).getTitle());
        tvSubtitle.setText(items.get(position).getSubtitle());
        ivIcon.setImageResource(items.get(position).getIcon());

        return view;
    }
}

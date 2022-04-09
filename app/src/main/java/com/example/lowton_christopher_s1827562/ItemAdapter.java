package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ItemAdapter extends ArrayAdapter<Item> {
    //Christopher Lowton - S1827562
    private Context context;
    private LinkedList<Item> items;

    public ItemAdapter(@NonNull Context context, int resource, LinkedList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        }

        Item item = items.get(position);

        RelativeLayout layout = (RelativeLayout) listItem.findViewById(R.id.relative_layout);

        Long worksLength = item.getWorksLength();

        //If less than 31 days (1 month) use green colour
        if (worksLength < 744) {
            layout.setBackgroundColor(Color.parseColor("#66CC66"));
            //If between 1 and 3 months use orange colour
        } else if (worksLength < 2232) {
            layout.setBackgroundColor(Color.parseColor("#FF9933"));
            //more than 3 months use red colour
        } else if (worksLength > 2232) {
            layout.setBackgroundColor(Color.parseColor("#CC3300"));
        }



        TextView title = (TextView) listItem.findViewById(R.id.title);
        title.setText(item.getTitle());

        TextView description = (TextView) listItem.findViewById(R.id.description);
        description.setText(item.shortString());

        return listItem;
    }
}

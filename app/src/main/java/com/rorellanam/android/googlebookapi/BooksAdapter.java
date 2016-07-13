package com.rorellanam.android.googlebookapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rorellanam on 6/28/16.
 */
public class BooksAdapter extends ArrayAdapter<Books> {

    public BooksAdapter(Context context, ArrayList<Books> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Books currentPlace = getItem(position);


        TextView namePlace = (TextView) listItemView.findViewById(R.id.title_book);
        namePlace.setText(currentPlace.getTitle());

        TextView address = (TextView) listItemView.findViewById(R.id.author);
        address.setText(currentPlace.getAuthor());

        TextView url = (TextView) listItemView.findViewById(R.id.url);
        url.setText(currentPlace.getUrl());

        return listItemView;
    }
}

package com.example.linhdq.searchapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.linhdq.searchapp.R;

import java.util.List;

/**
 * Created by linhdq on 2/18/17.
 */

public class SpinnerAdapter extends ArrayAdapter{
    private List<String> listSubject;
    private LayoutInflater inflater;

    public SpinnerAdapter(Context context, int resource, List<String> listSubject) {
        super(context, resource, listSubject);
        this.listSubject = listSubject;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView txtContent = (TextView) view.findViewById(R.id.txt_content);
        txtContent.setText(listSubject.get(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        TextView txtContent = (TextView) view.findViewById(R.id.txt_content_drop);
        txtContent.setText(listSubject.get(position));
        return view;
    }
}

package com.example.carpooltest1.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carpooltest1.Models.ReviewList;
import com.example.carpooltest1.R;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<ReviewList> {
    private List<ReviewList> Requestlist;
    private Context mctx;


    public ReviewListAdapter(List<ReviewList> requestlist, Context mctx) {
        super(mctx,R.layout.listreviewpending,requestlist);
        Requestlist = requestlist;
        this.mctx = mctx;
    }

    @Override
    public ReviewList getItem(int position) {
        return Requestlist.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(mctx);
        View view = inflator.inflate(R.layout.listreviewpending,null,true);

        TextView UserID = (TextView) view.findViewById(R.id.listreviewpending_UserITV);
        TextView DestLatTV = (TextView) view.findViewById(R.id.listreviewpending_Time);

        ReviewList Request = Requestlist.get(position);
        UserID.setText(Request.getUserID());
        DestLatTV.setText(String.valueOf(Request.getTime()));
        return view;
    }
}
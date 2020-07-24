package com.example.carpooltest1.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carpooltest1.Models.Reviews;
import com.example.carpooltest1.R;

import java.util.List;

public class ReviewsAdapter extends ArrayAdapter<Reviews> {
    private List<Reviews> Reviewslist;
    private Context mctx;

    public ReviewsAdapter(List<Reviews> LR, Context C)
    {
        super(C, R.layout.review, LR);
        this.Reviewslist = LR;
        this.mctx = C;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(mctx);
        View view = inflator.inflate(R.layout.review,null,true);

        TextView ReviewTxt = (TextView) view.findViewById(R.id.Reviews);
        ImageView star_1 = (ImageView) view.findViewById(R.id.star_1);
        ImageView star_2 = (ImageView) view.findViewById(R.id.star_2);
        ImageView star_3 = (ImageView) view.findViewById(R.id.star_3);
        ImageView star_4 = (ImageView) view.findViewById(R.id.star_4);
        ImageView star_5 = (ImageView) view.findViewById(R.id.star_5);

        Reviews Request = Reviewslist.get(position);
        ReviewTxt.setText(Request.getReview());

        switch (Request.getRating())
        {
            case 1:
                star_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                star_1.setVisibility(View.VISIBLE);
                star_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                star_1.setVisibility(View.VISIBLE);
                star_2.setVisibility(View.VISIBLE);
                star_3.setVisibility(View.VISIBLE);
                break;
            case 4:
                star_1.setVisibility(View.VISIBLE);
                star_2.setVisibility(View.VISIBLE);
                star_3.setVisibility(View.VISIBLE);
                star_4.setVisibility(View.VISIBLE);
                break;
            case 5:
                star_1.setVisibility(View.VISIBLE);
                star_2.setVisibility(View.VISIBLE);
                star_3.setVisibility(View.VISIBLE);
                star_4.setVisibility(View.VISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
        }

        return view;
    }
}
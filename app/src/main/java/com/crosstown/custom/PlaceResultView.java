package com.crosstown.custom;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.crosstown.R;
import com.crosstown.utilities.Utilities;

public class PlaceResultView extends ConstraintLayout
{
    private String mainText, secondaryText, distance, description, placeId;
    private String[] terms;
    private Context context = null;

    private ConstraintLayout layout;
    private ImageView locationIcon;
    private TextView mainTextTv, secondaryTextTv, distanceTv;

    public PlaceResultView(Context context)
    {
        super(context);
        this.context = context;

        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(service);

        layout = (ConstraintLayout)li.inflate(R.layout.place_result_view, this, true);

        mainTextTv = (TextView)layout.findViewById(R.id.mainText);
        secondaryTextTv = (TextView)layout.findViewById(R.id.secondaryText);
        locationIcon = (ImageView)layout.findViewById(R.id.locationIcon);
        distanceTv = (TextView)layout.findViewById(R.id.distance);
    }

    public PlaceResultView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;

        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(service);

        layout = (ConstraintLayout)li.inflate(R.layout.place_result_view, this, true);

        mainTextTv = (TextView)layout.findViewById(R.id.mainText);
        secondaryTextTv = (TextView)layout.findViewById(R.id.secondaryText);
        locationIcon = (ImageView)layout.findViewById(R.id.locationIcon);
        distanceTv = (TextView)layout.findViewById(R.id.distance);
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
        mainTextTv.setText(mainText);
    }
    public String getMainText() { return mainText; }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() { return description; }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
        secondaryTextTv.setText(secondaryText);
    }
    public String getSecondaryText() { return secondaryText; }

    public void setDistance(String distance) {
        this.distance = distance;
        distanceTv.setText(distance);
    }
    public String getDistance() { return distance; }

    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public String getPlaceId() { return placeId; }

    public void setTerms(String[] terms) { this.terms = terms; }
    public String[] getTerms() { return terms; }
}

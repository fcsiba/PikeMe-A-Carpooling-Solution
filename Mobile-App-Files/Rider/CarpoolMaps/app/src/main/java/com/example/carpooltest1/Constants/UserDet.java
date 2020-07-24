package com.example.carpooltest1.Constants;

import com.google.android.gms.maps.model.LatLng;

public class UserDet {
    public String BatchDeptRoll;
    private LatLng Source;
    private LatLng Dest;

    public UserDet(String batchDeptRoll) {
        BatchDeptRoll = batchDeptRoll;
    }

    public void setSource(LatLng source) {
        Source = source;
    }

    public void setDest(LatLng dest) {
        Dest = dest;
    }

    public String getBatchDeptRoll() {
        return BatchDeptRoll;
    }

    public LatLng getSource() {
        return Source;
    }

    public LatLng getDest() {
        return Dest;
    }
}

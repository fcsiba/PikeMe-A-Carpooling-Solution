package com.example.carpooltest1.Maps;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.Handlers.SharedPrefManager;
import com.example.carpooltest1.MainActivity;
import com.example.carpooltest1.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static android.support.constraint.Constraints.TAG;
import static com.example.carpooltest1.Constants.Error.COURSE_LOCATION;
import static com.example.carpooltest1.Constants.Error.CRITICALSECTION_ERROR;
import static com.example.carpooltest1.Constants.Error.DEFAULT_ZOOM;
import static com.example.carpooltest1.Constants.Error.FINE_LOCATION;
import static com.example.carpooltest1.Constants.Error.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.carpooltest1.Constants.Error.NED_LOCATION_MAIN_GATE;
import static com.example.carpooltest1.Constants.Error.NO_DRIVER_FOUND;
import static com.example.carpooltest1.Constants.Error.URL_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 */
public class MFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    Context context;
    GoogleMap map;
    Places places;
    PlacesClient placesClient;

    Button SrchBttn;
    TextView Src;
    TextView Dest;
    LatLng Source;
    LatLng Destination;
    LatLng BDCAST;
    String Driver_Selected;

    Switch ToNED;

    private Boolean CriticalSectionerror = false;

    private Boolean ToNed = false;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mfFusedLocationProviderClient;


    public MFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_m, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Initializing maps
        initMap();

        //Initializing Places
        initPlaces();


        Log.d(TAG, "onViewCreated: ");
        //Initialize textviews and search buttons
        Src = (TextView) getView().findViewById(R.id.SrcEdt);
        Dest = (TextView) getView().findViewById(R.id.DestEdt);
        SrchBttn = (Button) getView().findViewById(R.id.Srch);
        SrchBttn.setEnabled(false);

        SrchBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExAsyncTask task = new ExAsyncTask();
                task.execute();

            }
        });

        ToNED = (Switch) getView().findViewById(R.id.RDIOTND);


        ToNED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ToNed = true;
                }
                else{
                    ToNed = false;
                }
                map.clear();
                if(SrchBttn.isEnabled()){
                    SrchBttn.setEnabled(false);
                }
                UpdateMapsViews();
            }
        });

        Log.d(TAG, "onViewCreated: calling getDeviceLocation from OnViewCreated");

        //Getting Location permissions and device location
        getLocationPermission();
        getDeviceLocation();


        //UpdateMapsViews();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving the camera to: lat:  " + latLng.latitude + " long: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //hideSoftKeyboard();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        LatLng pp = new LatLng(24.9188,67.0889);
////        MarkerOptions options = new MarkerOptions();
////        options.position(pp).title("Test");
////        map.addMarker(options);
//        moveCamera(pp,DEFAULT_ZOOM,"My location");
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        Log.d(TAG, "onMapReady: Calling UpdateMapViews");
        UpdateMapsViews();
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    //My Locations
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission:  Getting Location permissions");
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                Log.d(TAG, "getLocationPermission: Permission Granted");
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting device's current location");
        mfFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {

                Task location = mfFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()&& task.getResult() != null) {
                            Log.d(TAG, "onComplete: getDeviceLocation: Location found");
                            Location currentLocation = (Location) task.getResult();
                            //Toast.makeText(MapActivity.this, "Moving Camera", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Moving camera to " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"My Location");

                        } else {
                            Log.d(TAG, "onComplete: Location not found set to NULL");
                            //Toast.makeText(MapActivity.this, "Current Lccation not found", Toast.LENGTH_SHORT);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }


    //Maps Initialization
    private void initMap(){
        Log.d(TAG, "initMap: called");

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        supportMapFragment.getMapAsync(this);

    }

    private void SetMapView(){

    }

    private void UpdateMapsViews(){

        map.clear();

        Src.setText("Source");
        Dest.setText("Destination");
        Src.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
        Dest.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
        if(ToNed){
            Dest.setText("IBA Main Campus");
            Dest.setTextSize(1,20);
//
//                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
//
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(NED_LOCATION_MAIN_GATE)
                    .title("Source")
                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_dest));
            map.addMarker(markerOptions);
//
//                    Src.setText(place.getName());
            Destination = NED_LOCATION_MAIN_GATE;
        }
        else{
            Src.setText("IBA Main Campus");
            Src.setTextSize(1,20);
//
//                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
//
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(NED_LOCATION_MAIN_GATE)
                    .title("Source")
                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
            map.addMarker(markerOptions);
//
//                    Src.setText(place.getName());
            Source = NED_LOCATION_MAIN_GATE;
        }
    }
    //Places Initializing

    private void initPlaces(){

        Log.d(TAG, "initPlaces: Called");
        Places.initialize(getContext(), getResources().getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(getActivity());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("PK");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d(TAG, "Place:(LAT,LNG) " + place.getLatLng() + ", " + place.getId());

//                if(Src.getText() == "" || Src.getText() == "Source"){
//                    Src.setTextSize(1,20);
//
//                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
//
//                    MarkerOptions markerOptions = new MarkerOptions()
//                            .position(place.getLatLng())
//                            .title("Source")
//                            .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
//                    map.addMarker(markerOptions);
//
//                    Src.setText(place.getName());
//                    Source = place.getLatLng();
//                }
//                else if(Dest.getText() == "" || Dest.getText() == "Destination"){
//                    Dest.setTextSize(1,20);
//
//                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
//
//                    MarkerOptions markerOptions = new MarkerOptions()
//                            .position(place.getLatLng())
//                            .title("Destination")
//                            .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_dest));
//                    map.addMarker(markerOptions);
//
//                    Dest.setText(place.getName());
//                    Destination = place.getLatLng();
//
//                    SrchBttn.setEnabled(true);

                //Polyline Draw Scenes

                if(ToNed) {
                    Src.setTextSize(1,20);

                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(place.getLatLng())
                            .title("Source")
                            .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
                    map.addMarker(markerOptions);
                    Src.setText(place.getName());
                    Source = place.getLatLng();
                }
                else{
                    Dest.setTextSize(1,20);

                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(place.getLatLng())
                            .title("Destination")
                            .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_dest));
                    map.addMarker(markerOptions);

                    Dest.setText(place.getName());
                    Destination = place.getLatLng();
                }
                DrawPolyLine();
                BDCAST = place.getLatLng();
                SrchBttn.setEnabled(true);
//                }
//                else{
//
//                }
            }

            private void FinalizeView(){

            }

            private void DrawPolyLine(){
                String url = getUrl(Source, Destination,null);
                Log.d(TAG, url.toString());
                FetchUrl FetchUrl = new FetchUrl();
                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                //move map camera
                map.moveCamera(CameraUpdateFactory.newLatLng(Source));
                map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
            }


            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
            }
        });
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

//Waypoint Drawer

    private class FetchUrl extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
// Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
// Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
// Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
// Connecting to url
            urlConnection.connect();
// Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
// Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
// Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
// Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
// Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
// Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
// Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                map.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest, LatLng [] waypoints) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String str_waypoints = "";
        if(waypoints != null){
            for (LatLng wp:
                    waypoints) {
                if(str_waypoints == ""){
                    str_waypoints = "waypoints=" + wp.latitude + "," + wp.longitude;
                }
                else{
                    str_waypoints += "%7C" + wp.latitude + "," + wp.longitude;
                }

            }
        }

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&"+ str_waypoints + "&" +  sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key=" + getResources().getString(R.string.google_maps_api_key);


        return url;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private class ExAsyncTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            String Id = null;
            JSONObject obj = new JSONObject();
            try {
                obj.put("UserID", SharedPrefManager.getInstance(getContext()).getUsername());
                obj.put("latitude", Double.toString(BDCAST.latitude));
                obj.put("longitude", Double.toString(BDCAST.longitude));
                obj.put("ToNED", (ToNed?"1":"0"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "doInBackground: JSONException");
            }

            try {
                Socket S = new Socket("192.168.18.215", 4056);
                PrintStream pr = new PrintStream(S.getOutputStream());

                pr.println(obj.toString().trim());

                Log.d(TAG, ": SOCKET CREATED!");
                BufferedReader br = new BufferedReader(new InputStreamReader(S.getInputStream(), StandardCharsets.UTF_8));
                Id = br.readLine();
                Log.d(TAG, "doInBackground: " + Id);
                Driver_Selected = Id;
                S.close();
                pr.close();
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: IO EXCEPTION");
            }


            Log.d(TAG, "doInBackground: returning ID");
            return Id;

        }

        @Override
        protected void onPostExecute(String s) {
//            TEST.setText(s);
            Log.d(TAG, "onPostExecute: Value of critical section " + String.valueOf(NO_DRIVER_FOUND));
            Log.d(TAG, "onPostExecute: " + s);
            if(s != null){
                if(s.trim() == String.valueOf(CRITICALSECTION_ERROR)){
                    Toast.makeText(getContext(),"Try Again!",Toast.LENGTH_LONG).show();
                }
                else if(s.trim() == String.valueOf(NO_DRIVER_FOUND).trim()){
                    Toast.makeText(getContext(),"No Driver Found!",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(),"Successful!",Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "onPostExecute: Socket Successful Results are: " +  s);
            }
        }
    }
}


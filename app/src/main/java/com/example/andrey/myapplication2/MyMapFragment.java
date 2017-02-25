package com.example.andrey.myapplication2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap myMap;
    MapFragment mapFragment;
    private View view;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);

            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);

            /*SupportMapFragment mapFragment2 = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment2.getMapAsync(this);*/

            mapFragment.getMapAsync(this);
        }
        catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Google Play Services missing!", Toast.LENGTH_LONG).show();
        }
        catch (InflateException e) {
            Toast.makeText(getActivity(), "Problems inflating the view!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        /**
         * 55.796806, 37.799302 Измайлово
         * 55.755814, 37.617635 Москва
         * добавить маркер myMap.addMarker(new MarkerOptions().title("Измайлово").position(new LatLng(55.796806, 37.799302)));
         * */
        LatLng izm = new LatLng(55.796806, 37.799302);
        LatLng center = new LatLng(55.755814, 37.617635);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(izm, 14));
        myMap.getUiSettings().setZoomControlsEnabled(false);
        myMap.getUiSettings().setZoomGesturesEnabled(true);
        myMap.addMarker(new MarkerOptions().title("Измайлово").position(izm));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
            myMap.setMyLocationEnabled(true);
        }
    }
}

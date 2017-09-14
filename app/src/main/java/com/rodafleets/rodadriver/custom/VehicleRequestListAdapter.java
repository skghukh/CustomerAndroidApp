package com.rodafleets.rodadriver.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rodafleets.rodadriver.R;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.model.VehicleType;
import com.rodafleets.rodadriver.utils.AppConstants;

import java.util.ArrayList;
import java.util.HashSet;

public class VehicleRequestListAdapter extends ArrayAdapter<VehicleRequest> {

    // Initialise custom font, for example:
    private Context context;
    private Typeface poppinsRegular;
    private Typeface poppinsSemiBold;

    private Bitmap greenIcon;
    private Bitmap redIcon;

    private ArrayList<VehicleRequest> vehicleRequests;

    private final HashSet<MapView> mMaps = new HashSet<MapView>();

    public VehicleRequestListAdapter(Context context, int resource, ArrayList<VehicleRequest> items) {
        super(context, resource, items);
        this.context = context;
        this.vehicleRequests = items;

        initMarkerBitmaps();

        initFonts();
    }


    private void initMarkerBitmaps(){

        int height = 45;
        int width = 32;

        greenIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_green);
        redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_red);

        greenIcon = Bitmap.createScaledBitmap(greenIcon, width, height, false);
        redIcon = Bitmap.createScaledBitmap(redIcon, width, height, false);
    }

    private void initFonts() {
        poppinsRegular = Typeface.createFromAsset(context.getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        poppinsSemiBold = Typeface.createFromAsset(context.getAssets(), AppConstants.FONT_POPPINS_SEMI_BOLD);
    }

    /*private view holder class*/
    private class ViewHolder implements OnMapReadyCallback {
        MapView mapView;

        TextView customerName;
        TextView requestDate;
        TextView amount;
        TextView distance;

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {

            MapsInitializer.initialize(context);
            map = googleMap;

            VehicleRequest data = (VehicleRequest) mapView.getTag();

            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
                mapView.setClickable(false);
            }
        }
    }

    private void setMapLocation(GoogleMap map, VehicleRequest vehicleRequest) {

        if (vehicleRequest != null) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            LatLng originLatLng = new LatLng(vehicleRequest.getOriginLat(), vehicleRequest.getOriginLng());

            MarkerOptions originMarkerOptions = new MarkerOptions();

            originMarkerOptions.position(originLatLng);
            originMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(greenIcon));

            Marker originMarker = map.addMarker(originMarkerOptions);

            builder.include(originMarker.getPosition());

            LatLng destinationLatLng = new LatLng(vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());

            MarkerOptions destinationMarkerOptions = new MarkerOptions();

            destinationMarkerOptions.position(destinationLatLng);
            destinationMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(redIcon));

            Marker destinationMarker = map.addMarker(destinationMarkerOptions);
            builder.include(destinationMarker.getPosition());

            LatLngBounds bounds = builder.build();

            int padding = 10; // offset from edges of the map in pixels

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        }

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        VehicleRequest request = vehicleRequests.get(position);

        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(rowView == null) {
            rowView = inflater.inflate(R.layout.vehicle_request_list_view_item, parent, false);

            holder = new ViewHolder();
            holder.mapView = (MapView) rowView.findViewById(R.id.vehicle_request_map);
            holder.customerName = (TextView) rowView.findViewById(R.id.vehicle_request_customer_name);
            holder.amount = (TextView) rowView.findViewById(R.id.vehicle_request_amount);
            holder.requestDate = (TextView) rowView.findViewById(R.id.vehicle_request_date);
            holder.distance = (TextView) rowView.findViewById(R.id.vehicle_request_distance);

            holder.customerName.setTypeface(poppinsRegular);
            holder.amount.setTypeface(poppinsSemiBold);
            holder.requestDate.setTypeface(poppinsRegular);
            holder.distance.setTypeface(poppinsRegular);

            rowView.setTag(holder);

            // Initialise the MapView
            holder.initializeMapView();

            // Keep track of MapView
            mMaps.add(holder.mapView);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

//        holder.customerName.setText(request.getCustomerName());
//        holder.amount.setText(request.getApproxFareInCents());
//        holder.requestDate.setText(request.getDistance());
//        holder.distance.setText(request.getDistance());
        holder.mapView.setTag(request);

        if (holder.map != null) {
            // The map is already ready to be used
            setMapLocation(holder.map, request);
        }

        return rowView;
    }
}

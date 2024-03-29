package com.example.mappi;

import static com.example.mappi.R.id.traffic_toggle_fab;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapNav extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    //private static final String SOURCE_ID = "SOURCE_ID";
    //private static final String ICON_ID = "ICON_ID";
    //private static final String LAYER_ID = "LAYER_ID";

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Button startNavButton;
    private Button shareButton;

    // variables for adding location layer
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button button;
    private FloatingActionButton location_search;
    private String geoJsonSourceLayerId = "GeoJsonSourceLayerId";
    private String symbolIconId = "SymbolIconId";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private BuildingPlugin buildingPlugin;
    private TrafficPlugin trafficPlugin;

    private static Point currentPoint, destinationPoint;
    private FirebaseAuth mAuth;
    private static String method, method1;
    User users;
    sqlDB db;
    private static String mMode;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this , getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map_nav);

        mapView = findViewById(R.id.mapView);
        shareButton = findViewById(R.id.shareButton);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point location = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                        locationComponent.getLastKnownLocation().getLatitude());
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent .setType("text/plain");
                txtIntent .putExtra(android.content.Intent.EXTRA_TEXT, "My Location: " + location.latitude() + ", " + location.longitude());
                startActivity(Intent.createChooser(txtIntent ,"Share"));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
//
//    @Override
//    protected void onPostCreate(@Nullable Bundle outState) {
//        super.onPostCreate(outState);
//        mapView.onSaveInstanceState(outState);
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(com.mapbox.services.android.navigation.ui.v5.R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                SharedPreferences sharedPreferences = getSharedPreferences("UserPreference", MODE_PRIVATE);
                String landmark = sharedPreferences.getString("landmark", "");

                MarkerOptions markerOptions = new MarkerOptions();

                if (landmark.equals("Historical")) {
                    markerOptions.title("Emmanuel Cathedral");
                    markerOptions.position(new LatLng(-29.8574342439, 31.0156402848));//-29.8574342439, 31.0156402848
                    markerOptions.snippet(" The Emmanuel Cathedral or simply Cathedral of Durban, is the name given to the Catholic Church which is situated at the heart of Durban.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                    markerOptions.title("Durban Art Gallery");
                    markerOptions.position(new LatLng(-29.8575979, 31.0356319));//-29.8575979, 31.0356319
                    markerOptions.snippet("A pioneering national gallery and the first to recognise African craft as art in the 1970's.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                    markerOptions.title("Durban Natural Science Museum");
                    markerOptions.position(new LatLng(-29.858722868725167, 31.02667587121167));
                    markerOptions.snippet("Science museum featuring exhibits on the history of the Earth, including past & present life.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                } else if (landmark.equals("Modern")) {
                    markerOptions.title("Umhlanga Arch");
                    markerOptions.position(new LatLng(-29.727618529595105, 31.0729009405428));
                    markerOptions.snippet("Umhlanga Arch is Durban's new lifestyle hub in Umhlanga. From luxury apartments and flexi workspaces to shops, restaurants, bars, and live events.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));


                    markerOptions.title("Victoria Street Market");
                    markerOptions.position(new LatLng(-29.8566989, 331.0154723 ));
                    markerOptions.snippet("Colloquially known as Vic, this market dates back to the 1800s and is the oldest one in the city. It is one of the most fun things to do in Durban.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                    markerOptions.title("Moses Mabhida Stadium");
                    markerOptions.position(new LatLng(-29.82649, 31.03054));
                    markerOptions.snippet("This stadium was first built for the 2010 FIFA World Cup. Though designed for football, the stadium is also used for a variety of sports including cricket, rugby, motorsports, and so on.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));
                } else {
                    markerOptions.title("Durban Beach");
                    markerOptions.position(new LatLng(-29.833747097689628, 31.036676704116775));
                    markerOptions.snippet("Soak off in the warm water of Durban's beach.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                    //Durban Botanic Gardens is -29.84179, and the longitude is 31.00296.
                    markerOptions.title("Durban Botanic Gardens");
                    markerOptions.position(new LatLng(-29.84179, 31.00296));
                    markerOptions.snippet("Durban's oldest public institution and Africa's oldest surviving botanical gardens.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));

                    markerOptions.title("uShaka Marine World");//-29.867687 ; Longitude: 31.047045.
                    markerOptions.position(new LatLng(29.867687, 31.047045));
                    markerOptions.snippet("A 16-hectare theme park containing 10,000 animal species and a total capacity of 4.6 million gallons.\n\nFavourite");
                    mapboxMap.addMarker((markerOptions));
                }

                buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
                buildingPlugin.setMinZoomLevel(15f);
                buildingPlugin.setVisibility(true);

                trafficPlugin = new TrafficPlugin(mapView, mapboxMap, style);
                trafficPlugin.setVisibility(true);

                TrafficPlugin trafficPlugin = new TrafficPlugin(mapView, mapboxMap, style);

                // Enable the traffic view by default
                trafficPlugin.setVisibility(true);

                findViewById(traffic_toggle_fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mapboxMap != null) {
                            trafficPlugin.setVisibility(!trafficPlugin.isVisible());
                        }
                    }
                });

                saveRouteHistory();

                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);

                mapboxMap.addOnMapClickListener(MapNav.this);
                button = findViewById(R.id.startNavButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean simulateRoute = true;
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(simulateRoute)
                                .build();
                        double distance = currentRoute.distance() / 1000;
                        distance = Math.round(distance * 100.0) / 100.0;
                        double durationDouble = currentRoute.duration();
                        int minutes = (int)durationDouble % 3600 / 60;
                        Toast.makeText(MapNav.this, "Current Route: " + distance + " km (" + minutes + " minutes)", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("Last Trip", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("Distance", String.valueOf(distance)).apply();
                        sharedPreferences.edit().putString("Duration", String.valueOf(durationDouble)).apply();
                        // Call this method with Context from within an Activity
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(MapNav.this, options);

                    }
                });
                initSearch();
                setUpSource(style);

                setLayer(style);

                Drawable drawable = ResourcesCompat.getDrawable(getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default, null);
                Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                style.addImage(symbolIconId, bitmap);
            }
        });
    }

    private void setLayer(Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})));
    }

    private void setUpSource(Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceLayerId));
    }

    private void initSearch() {
        location_search = findViewById(R.id.search_button);
        location_search.setOnClickListener(v -> {
            Intent intent= new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ?
                            Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);

            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    // Method to save all data from the route to the database
    private void saveRouteHistory() {
        try {
            boolean save;
            // Date cal = format(Calender.getInstance().getTime());
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String cal = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            String currentDateTime = cal;

            //get user current id
            // String userId = "";
            String userId;
            userId = mAuth.getCurrentUser().getUid();

            LatLng startLatLng = new LatLng(currentPoint.latitude(), currentPoint.longitude());
            LatLng destinationLatLng = new LatLng(destinationPoint.latitude(), destinationPoint.longitude());
            String transport = method;
            Toast.makeText(this, transport, Toast.LENGTH_SHORT).show();

            Toast.makeText(this, "History Updated", Toast.LENGTH_SHORT).show();
            save = db.insertUserHistory(userId, currentDateTime, transport, startLatLng, destinationLatLng);

            if (save) {
                Toast.makeText(this, "Route saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save route", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            Toast.makeText(this, "No route detected yet!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geoJsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude())).zoom(14)
                            .build()), 4000);
                }
            }
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);
        button.setBackgroundResource(com.mapbox.mapboxsdk.R.color.mapbox_blue);
        return true;
    }



    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .voiceUnits(DirectionsCriteria.METRIC)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, com.mapbox.services.android.navigation.ui.v5.R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    }


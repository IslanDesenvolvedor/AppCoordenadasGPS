package com.icodelt.appcoordenadasgps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    GoogleMap mMap;

    TextView textViewLatitude, textViewLongitude;

    double latitude, longitude;
    boolean gpsAtivo;

    LocationManager locationManager;

    String[] permissoesRequiridas = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int APP_PERMISSOES_ID = 2024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewLatitude = findViewById(R.id.textViewLatitude);
        textViewLongitude = findViewById(R.id.textViewLongitude);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(MainActivity.this);

        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        gpsAtivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsAtivo) {
            obterCoordenadas();
        } else {
            latitude = 0.00;
            longitude = 0.00;

            textViewLatitude.setText("Latitude: " + latitude);
            textViewLongitude.setText("Longitude: " + longitude);
        }
    }

    private void obterCoordenadas() {

        boolean permissaoAtiva = solicitarPermissao();

        if (permissaoAtiva) {
            capturarUltimaLocalizacaoValida();
        }
    }

    private boolean solicitarPermissao() {
        List<String> permissoesNegadas = new ArrayList<>();

        int permissaoNegada;

        for (String permissao : this.permissoesRequiridas) {

            permissaoNegada = ContextCompat.checkSelfPermission(MainActivity.this, permissao);

            if (permissaoNegada != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(permissao);
            }
        }

        if (!permissoesNegadas.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissoesNegadas.toArray(new String[permissoesNegadas.size()]), APP_PERMISSOES_ID);
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    private void capturarUltimaLocalizacaoValida() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        } else {
            latitude = 0.00;
            longitude = 0.00;

        }
        textViewLatitude.setText("Latitude: " + formatarGeopoint(latitude));
        textViewLongitude.setText("Longitude: " + formatarGeopoint(longitude));
    }

    private String formatarGeopoint(double valor) {

        DecimalFormat decimalFormat = new DecimalFormat("#.######");

        return decimalFormat.format(valor);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng localizacao = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(localizacao).title("Seu dispositivo aqui!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(localizacao));
    }
}
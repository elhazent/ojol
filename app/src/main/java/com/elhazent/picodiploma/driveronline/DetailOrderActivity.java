package com.elhazent.picodiploma.driveronline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elhazent.picodiploma.driveronline.helper.DirectionMapsV2;
import com.elhazent.picodiploma.driveronline.helper.HeroHelper;
import com.elhazent.picodiploma.driveronline.helper.MyContants;
import com.elhazent.picodiploma.driveronline.helper.SessionManager;
import com.elhazent.picodiploma.driveronline.model.DataItem;
import com.elhazent.picodiploma.driveronline.model.ResponseHistoryReq;
import com.elhazent.picodiploma.driveronline.model.ResponseWaypoint;
import com.elhazent.picodiploma.driveronline.model.RoutesItem;
import com.elhazent.picodiploma.driveronline.network.InitRetrofit;
import com.elhazent.picodiploma.driveronline.network.RestApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.txtidbooking)
    TextView txtidbooking;
    @BindView(R.id.requestFrom)
    TextView requestFrom;
    @BindView(R.id.requestTo)
    TextView requestTo;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.requestWaktu)
    TextView requestWaktu;
    @BindView(R.id.requestTarif)
    TextView requestTarif;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.requestNama)
    TextView requestNama;
    @BindView(R.id.requestEmail)
    TextView requestEmail;
    @BindView(R.id.requestID)
    TextView requestID;
    @BindView(R.id.requestTakeBooking)
    Button requestTakeBooking;
    @BindView(R.id.CompleteBooking)
    Button CompleteBooking;
    @BindView(R.id.chatuser)
    Button chatuser;
    private GoogleMap mMap;
    int index;
    int status;
    DataItem DataItem;
    SessionManager session;
    String iddriver;
    String token;
    String device;
    String idbooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);
        index = getIntent().getIntExtra(MyContants.INDEX, 0);
        status = getIntent().getIntExtra(MyContants.STATUS, 0);
        if (status == 1) {
            requestTakeBooking.setVisibility(View.VISIBLE);
            CompleteBooking.setVisibility(View.GONE);
            DataItem = HistoryFragment.DataItem.get(index);
        } else if (status == 2) {
            requestTakeBooking.setVisibility(View.GONE);
            chatuser.setVisibility(View.VISIBLE);
            CompleteBooking.setVisibility(View.VISIBLE);
            DataItem = HistoryFragment.DataItem2.get(index);
        } else {
            requestTakeBooking.setVisibility(View.GONE);
            CompleteBooking.setVisibility(View.GONE);
            chatuser.setVisibility(View.GONE);
            DataItem = HistoryFragment.DataItem3.get(index);
        }
        detailRequest();
        session = new SessionManager(this);
        iddriver = session.getIdUser();
        token = session.getToken();
        device = HeroHelper.getDeviceUUID(this);
        idbooking = DataItem.getIdBooking();
    }

    @SuppressLint("SetTextI18n")
    private void detailRequest() {
        requestFrom.setText("dari : " + DataItem.getBookingFrom());
        requestTo.setText("tujuan : " + DataItem.getBookingTujuan());
        requestTarif.setText("dari : " + DataItem.getBookingBiayaUser());
        requestWaktu.setText("dari : " + DataItem.getBookingJarak());
        requestNama.setText("dari : " + DataItem.getUserNama());
        requestEmail.setText("dari : " + DataItem.getUserEmail());
        txtidbooking.setText("dari : " + DataItem.getIdBooking());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        detailMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + DataItem.getBookingTujuanLat()
                                + "," + DataItem.getBookingTujuanLng()));
                startActivity(i);
            }
        });
    }

    private void detailMap() {
        //get koordinat
        String origin = String.valueOf(DataItem.getBookingFromLat()) + "," + String.valueOf(DataItem.getBookingFromLng());
        String desti = String.valueOf(DataItem.getBookingTujuanLat()) + "," + String.valueOf(DataItem.getBookingTujuanLng());

        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(Double.parseDouble(DataItem.getBookingFromLat()), Double.parseDouble(DataItem.getBookingFromLng())));
        bound.include(new LatLng(Double.parseDouble(DataItem.getBookingTujuanLat()), Double.parseDouble(DataItem.getBookingTujuanLng())));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));
        LatLngBounds bounds = bound.build();
// begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
// end of new code

        mMap.animateCamera(cu);

        RestApi service = InitRetrofit.getInstanceGoogle();
        String api = getString(R.string.google_maps_key);
        Call<ResponseWaypoint> call = service.setRute(origin, desti, api);
        call.enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
                List<RoutesItem> routes = response.body().getRoutes();

                DirectionMapsV2 direction = new DirectionMapsV2(DetailOrderActivity.this);
                try {
                    String points = routes.get(0).getOverviewPolyline().getPoints();
                    direction.gambarRoute(mMap, points);

                } catch (Exception e) {
                    Toast.makeText(DetailOrderActivity.this, "lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.requestTakeBooking, R.id.CompleteBooking, R.id.chatuser})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.requestTakeBooking:
                takeBooking();
                break;
            case R.id.CompleteBooking:
                completeBooking();
                break;
            case R.id.chatuser:
                chatUser();
                break;
        }
    }

    private void chatUser() {
        startActivity(new Intent(DetailOrderActivity.this, ChatActivity.class));
    }

    private void completeBooking() {
        InitRetrofit.getInstance().completeBooking(iddriver, idbooking, device, token).enqueue(new Callback<ResponseHistoryReq>() {
            @Override
            public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(DetailOrderActivity.this, "selamat" + msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DetailOrderActivity.this, HistoryActivity.class));
                    } else {
                        Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {
                Toast.makeText(DetailOrderActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void takeBooking() {
        InitRetrofit.getInstance().takeBooking(iddriver, idbooking, device, token).enqueue(new Callback<ResponseHistoryReq>() {
            @Override
            public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {
                Toast.makeText(DetailOrderActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

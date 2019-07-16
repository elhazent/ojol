package com.elhazent.picodiploma.driveronline;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elhazent.picodiploma.driveronline.helper.HeroHelper;
import com.elhazent.picodiploma.driveronline.helper.SessionManager;
import com.elhazent.picodiploma.driveronline.model.DataItem;
import com.elhazent.picodiploma.driveronline.model.ResponseHistoryReq;
import com.elhazent.picodiploma.driveronline.model.User;
import com.elhazent.picodiploma.driveronline.network.InitRetrofit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment {
    int i;
    @BindView(R.id.recyclerV)
    RecyclerView recyclerview;
    Unbinder unbinder;
    private SessionManager manager;
    public static List<DataItem> DataItem;
    public static List<DataItem> DataItem2;
    public static List<DataItem> DataItem3;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryFragment(int i) {
        this.i = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proses, container, false);
        unbinder = ButterKnife.bind(this, view);
        manager = new SessionManager(getActivity());

        getDataItem();

        return view;
    }

    private void getDataItem() {
        String token = manager.getToken();
        String iduser = manager.getIdUser();
        String device = HeroHelper.getDeviceUUID(getActivity());


        if (i == 1) {
            InitRetrofit.getInstance().getHistoryRequest().enqueue(new Callback<ResponseHistoryReq>() {
                @Override
                public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        DataItem = response.body().getData();
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(DataItem, getActivity(), i);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {
                    Toast.makeText(getContext(), "GAGAL"+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FAILURE", "onFailure: " + t.getLocalizedMessage() );
                }
            });
        } else if (i == 2) {

            InitRetrofit.getInstance().getHistoryProses(iduser, device, token).enqueue(new Callback<ResponseHistoryReq>() {
                @Override
                public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        DataItem2 = response.body().getData();
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(DataItem2, getActivity(), i);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {

                }
            });
        } else {
            InitRetrofit.getInstance().getHistoryComplete(iduser, device, token).enqueue(new Callback<ResponseHistoryReq>() {
                @Override
                public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        DataItem3 = response.body().getData();
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(DataItem3, getActivity(), i);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {

                }
            });
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



}

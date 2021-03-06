package com.groupfive.satapp.ui.inventariable.locationinventariable;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupfive.satapp.R;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.listeners.ILocationListener;

import java.util.List;

public class LocationFragment extends Fragment {


    private int mColumnCount = 1;
    private ILocationListener mListener;
    private List<String> locationModelList;
    private InventariableViewModel inventariableViewModel;
    private MyLocationRecyclerViewAdapter adapter;
    private Context context;


    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inventariableViewModel = ViewModelProviders.of(getActivity()).get(InventariableViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new MyLocationRecyclerViewAdapter(context, locationModelList, mListener);
            recyclerView.setAdapter(adapter);

          loadLocations();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILocationListener) {
            mListener = (ILocationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ILocationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadLocations() {
        inventariableViewModel.getLocations().observe(getActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> locationModels) {
                locationModelList = locationModels;
                adapter.setData(locationModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

}

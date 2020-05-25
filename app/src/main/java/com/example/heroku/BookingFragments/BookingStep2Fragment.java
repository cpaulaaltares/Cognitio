package com.example.heroku.BookingFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroku.Common.Common;
import com.example.heroku.Common.SpacesItemDecoration;
import com.example.heroku.R;
import com.example.heroku.adapters.MyPsychologyCategoryAdapter;
import com.example.heroku.models.PsychologyCategory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_psychologyCategory)
    RecyclerView recycler_psychologyCategory;

    private BroadcastReceiver psychologyCategoryDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<PsychologyCategory>  psychologyCategoryArrayList = intent.getParcelableArrayListExtra(Common.KEY_OFFICE_LOAD_DONE);
            //Create adapter
            MyPsychologyCategoryAdapter adapter = new MyPsychologyCategoryAdapter(getContext(), psychologyCategoryArrayList);
            recycler_psychologyCategory.setAdapter(adapter);
        }
    };

    static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance(){
        if(instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(psychologyCategoryDoneReceiver, new IntentFilter(Common.KEY_OFFICE_LOAD_DONE));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main,menu);

        //SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        menu.findItem(R.id.action_add_post).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onDestroy() {
            localBroadcastManager.unregisterReceiver(psychologyCategoryDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_two, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        initView();

        return itemView;
    }

    private void initView() {
        recycler_psychologyCategory.setHasFixedSize(true);
        recycler_psychologyCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recycler_psychologyCategory.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_psychologyCategory.addItemDecoration(new SpacesItemDecoration(4));
    }
}

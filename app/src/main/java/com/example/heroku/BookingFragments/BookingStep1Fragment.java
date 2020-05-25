package com.example.heroku.BookingFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroku.Common.Common;
import com.example.heroku.Common.SpacesItemDecoration;
import com.example.heroku.Interface.IAllOfficeLoadListener;
import com.example.heroku.Interface.IBranchLoadListener;

import com.example.heroku.R;
import com.example.heroku.adapters.MyOfficeAdapter;
import com.example.heroku.models.ModelOffice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IAllOfficeLoadListener, IBranchLoadListener {
    private Unbinder unbinder;

    //Variable
    CollectionReference allOfficeRef;
    CollectionReference branchRef;


    IAllOfficeLoadListener iAllOfficeLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_office)
    RecyclerView recycler_office;

    //RecyclerView recycler_office;



    AlertDialog dialog;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance(){
        if(instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.my_layout);

        allOfficeRef = FirebaseFirestore.getInstance().collection("PsychoTherapist");
        iAllOfficeLoadListener = this;
        iBranchLoadListener = this;

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        recycler_office = itemView.findViewById(R.id.recycler_office);
        initView();

        loadAllOffice();

        return itemView;
    }

    private void initView() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recycler_office.setHasFixedSize(true);
        //recycler_office.setLayoutManager(manager);
        recycler_office.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_office.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllOffice() {
        allOfficeRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<String> list = new ArrayList<>();
                            list.add("Please choose city");//added to dropdown
                            for(QueryDocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult()))
                            {
                                list.add(documentSnapshot.getId());
                            }

                            iAllOfficeLoadListener.onAllOfficeLoadSuccess(list);
                            //Toast.makeText(getActivity(), "Load all office success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllOfficeLoadListener.onAllOfficeLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllOfficeLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position > 0){
                    loadBranchOfCity(item.toString());
                }
                else {
                    recycler_office.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBranchOfCity(String cityName) {
        dialog.show();

        Common.city = cityName;

        branchRef = FirebaseFirestore.getInstance()
                .collection("PsychoTherapist")
                .document(cityName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ModelOffice> list = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                        {
                           ModelOffice office = documentSnapshot.toObject(ModelOffice.class);
                           office.setOfficeId(documentSnapshot.getId());
                           list.add(office);

                        }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllOfficeLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<ModelOffice> officeList) {
        MyOfficeAdapter adapter = new MyOfficeAdapter(getActivity(), officeList);
        recycler_office.setAdapter(adapter);

        recycler_office.setVisibility(View.VISIBLE);
        //Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}

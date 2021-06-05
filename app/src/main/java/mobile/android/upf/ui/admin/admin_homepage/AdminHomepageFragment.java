package mobile.android.upf.ui.admin.admin_homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_admin;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_restaurant;
import mobile.android.upf.data.model.Restaurant;

public class AdminHomepageFragment extends Fragment {

    private AdminHomepageViewModel adminHomepageViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private DatabaseReference mDatabase;

    private List<Restaurant> lstRest;
    private RecyclerView myrv;
    private RecyclerViewAdapter_admin myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminHomepageViewModel =
                new ViewModelProvider(this).get(AdminHomepageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_admin, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante

                        if (String.valueOf(restaurant.child("status").getValue()).equals("0") &&
                            String.valueOf(restaurant.child("admin_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("admin_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }

                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_admin_restaurants);
                    myAdapter = new RecyclerViewAdapter_admin(getActivity(), lstRest, AdminHomepageFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_restaurant_admin);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    public void updateRecycler(){
        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante

                        if (String.valueOf(restaurant.child("status").getValue()).equals("0") &&
                            String.valueOf(restaurant.child("admin_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("admin_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }


                    }


                    myAdapter = new RecyclerViewAdapter_admin(getActivity(), lstRest, AdminHomepageFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 0) {
                    filter(String.valueOf(s));
                } else {
                    myAdapter = new RecyclerViewAdapter_admin(getActivity(), lstRest, AdminHomepageFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    filter(String.valueOf(s));
                } else {
                    myAdapter = new RecyclerViewAdapter_admin(getActivity(), lstRest, AdminHomepageFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);


    }

    private void filter(String text) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant item : lstRest) {
//            Log.d("RICERCATO", text);
//            Log.d("RICERCA", item.getName());
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new RecyclerViewAdapter_admin(getActivity(), filteredList, AdminHomepageFragment.this);

        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        myrv.setAdapter(myAdapter);
    }
}
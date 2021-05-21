package mobile.android.upf.ui.client.client_restaurants;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_restaurant;
import mobile.android.upf.data.model.Restaurant;

public class ClientRestaurantsFragment extends Fragment {

    private ClientRestourantsViewModel clientRestourantsViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    Context context;
    MaterialCardView cardview;
    ViewGroup.LayoutParams layoutparams;
    LinearLayout linearLayout;

    RecyclerView myrv;
    RecyclerViewAdapter_client_restaurant myAdapter;

    List<Restaurant> lstRest;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientRestourantsViewModel =
                new ViewModelProvider(this).get(ClientRestourantsViewModel.class);
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_restaurants_client, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText searchBox = (EditText) root.findViewById(R.id.edit_text_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    filter(String.valueOf(s));
                } else {
                    myAdapter = new RecyclerViewAdapter_client_restaurant(getActivity(), lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant: restaurants_database) {
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));

                        if (Integer.parseInt(String.valueOf(restaurant.child("status").getValue())) == 1) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }
                    }


                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_client_restaurants);
                    myAdapter = new RecyclerViewAdapter_client_restaurant(getActivity(), lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }
        });

        return root;
    }

//    public void onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//        Log.d("MENU", "CIAO");
//
//        MenuItem searchItem = menu.findItem(R.id.actionSearch);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return false;
//            }
//        });
//
//    }

    private void filter(String text) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant item : lstRest) {
            Log.d("RICERCATO", text);
            Log.d("RICERCA", item.getName());
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new RecyclerViewAdapter_client_restaurant(getActivity(), filteredList);

        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        myrv.setAdapter(myAdapter);
    }
}
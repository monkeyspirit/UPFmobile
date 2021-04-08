package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import mobile.android.upf.AddRestaurantActivity;
import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter_restaurant;
import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.ui.client.client_homepage.ClientEditOrdersFragment;

public class RestaurantRestaurantsFragment extends Fragment {

    private RestaurantRestourantsViewModel restaurantRestourantsViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    Context context;
    MaterialCardView cardview;
    ViewGroup.LayoutParams layoutparams;
    LinearLayout linearLayout;

    RecyclerView myrv;
    RecyclerViewAdapter_restaurant myAdapter;

    List<Restaurant> lstRest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantRestourantsViewModel =
                new ViewModelProvider(this).get(RestaurantRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_restaurant, container, false);

//        Floating button per l'aggiunta di nuovi ristoranti
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRestaurantActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

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
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.valueOf(String.valueOf(restaurant.child("status").getValue()))));
                        }

                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_restaurant_restaurants);
                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                    ItemTouchHelper itemTouchHelperSwipe = new ItemTouchHelper(simpleCallbackSwipe);
                    itemTouchHelperSwipe.attachToRecyclerView(myrv);
                }


            }

            Restaurant swipedItem = null;
            //Gestisco gli swipe a destra e sinistra
            ItemTouchHelper.SimpleCallback simpleCallbackSwipe = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                    int pos = viewHolder.getAdapterPosition();

                    switch (direction) {
                        // DELETE
                        case ItemTouchHelper.LEFT:
//                            RestaurantDeleteFragment dialogDeleteFragment = new RestaurantDeleteFragment();
//                            dialogDeleteFragment.show(getChildFragmentManager(), "DeleteFragment");
//
//                            //dialogDeleteFragment.setTargetFragment(getTargetFragment(), 0);
//
//                            //getTargetFragment().onActivityResult(getTargetRequestCode());

                            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext())
                                    // set message, title, and icon
                                    .setTitle("Delete")
                                    .setMessage(getString(R.string.confirm_delete))
                                    .setIcon(R.drawable.ic_baseline_delete_24)


                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //your deleting code
                                            swipedItem = lstRest.get(pos);
                                            lstRest.remove(pos);
                                            myAdapter.notifyItemRemoved(pos);
                                            dialog.dismiss();
                                        }

                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            swipedItem = lstRest.get(pos);
                                            lstRest.remove(pos);
                                            myAdapter.notifyItemRemoved(pos);
                                            lstRest.add(pos, swipedItem);
                                            myAdapter.notifyItemInserted(pos);

                                            dialog.dismiss();

                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            swipedItem = lstRest.get(pos);
                                            lstRest.remove(pos);
                                            myAdapter.notifyItemRemoved(pos);
                                            lstRest.add(pos, swipedItem);
                                            myAdapter.notifyItemInserted(pos);

                                            dialog.dismiss(); //forse non serve nemmeno
                                        }
                                    })
                                    .create();
                            myQuittingDialogBox.show();

                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                            break;




                        // MODIFY
                        case ItemTouchHelper.RIGHT:
                            ClientEditOrdersFragment dialogEditOrderFragment = new ClientEditOrdersFragment();
                            dialogEditOrderFragment.show(getChildFragmentManager(), "EditFragment");

                            swipedItem = lstRest.get(pos);
                            lstRest.remove(pos);
                            myAdapter.notifyItemRemoved(pos);
                            //sarà necessario fare un aggiornamento dei dati
                            lstRest.add(pos, swipedItem);
                            myAdapter.notifyItemInserted(pos);
                            break;
                    }

                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.design_default_color_error))
                            .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.design_default_color_secondary))
                            .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

        });

        return root;
    }
}
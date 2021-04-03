package mobile.android.upf.ui.client.client_homepage;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter;

public class ClientOrdersFragment extends Fragment {

    private ClientOrdersViewModel clientOrdersViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    Context context;
    MaterialCardView cardview;
    LayoutParams layoutparams;
    LinearLayout linearLayout;

    RecyclerView myrv;
    RecyclerViewAdapter myAdapter;

    //    Solo di prova
    List<Order> lstOrder;
//


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientOrdersViewModel = new ViewModelProvider(this).get(ClientOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_client, container, false);

        lstOrder = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String name = String.valueOf(task.getResult().child("name").getValue());
                    String surname = String.valueOf(task.getResult().child("surname").getValue());
                    String address = String.valueOf(task.getResult().child("address").getValue());
                    lstOrder.add(new Order(name, name, name));
                    lstOrder.add(new Order(surname, surname, surname));
                    lstOrder.add(new Order(address, address, address));
                    lstOrder.add(new Order(name, name, name));
                    lstOrder.add(new Order(surname, surname, surname));
                    lstOrder.add(new Order(address, address, address));
                    lstOrder.add(new Order(name, name, name));
                    lstOrder.add(new Order(surname, surname, surname));
                    lstOrder.add(new Order(address, address, address));

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_client_orders);
                    myAdapter = new RecyclerViewAdapter(getActivity(), lstOrder);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

//                    HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getValue();
//                    for(Map.Entry<String, Object> entry: data.entrySet()) {
////                        createCards(entry.getKey(), entry.getValue(), root);
//
//                        Log.d("firebase", String.valueOf(entry.getKey() + " : " + entry.getValue()));
//                    }

                    ItemTouchHelper itemTouchHelperDragDrop = new ItemTouchHelper(simpleCallbackDragDrop);
                    itemTouchHelperDragDrop.attachToRecyclerView(myrv);

                    ItemTouchHelper itemTouchHelperSwipe = new ItemTouchHelper(simpleCallbackSwipe);
                    itemTouchHelperSwipe.attachToRecyclerView(myrv);
                }


            }

            //Gestisco il drag and drop
            ItemTouchHelper.SimpleCallback simpleCallbackDragDrop = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();

                    Collections.swap(lstOrder, fromPosition, toPosition);
                    recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }
            };

            Order deletedItem = null;
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
                        case ItemTouchHelper.LEFT: //cancello l'elemento
                            deletedItem = lstOrder.get(pos);
                            lstOrder.remove(pos);
                            myAdapter.notifyItemRemoved(pos);
                            Snackbar.make(myrv, deletedItem.toString(), Snackbar.LENGTH_LONG).setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    lstOrder.add(pos, deletedItem);
                                    myAdapter.notifyItemInserted(pos);
                                }
                            }).show();

                            break;
                        case ItemTouchHelper.RIGHT: //modifico l'elemento
                            ClientEditOrdersFragment dialogEditOrderFragment = new ClientEditOrdersFragment();
                            dialogEditOrderFragment.show(getChildFragmentManager(), "EditFragment");

                            deletedItem = lstOrder.get(pos);
                            lstOrder.remove(pos);
                            myAdapter.notifyItemRemoved(pos);
                            //sarà necessario fare un aggiornamento dei dati
                            lstOrder.add(pos, deletedItem);
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


//        clientOrdersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

}
package mobile.android.upf.ui.restaurant.restaurant_notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Notification;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_restaurant_notification;

public class RestaurantNotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private RecyclerView myrv;
    private RecyclerViewAdapter_restaurant_notification myAdapter;

    private List<Notification> lstNotification;
    private String userId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RestaurantNotificationsViewModel restaurantNotificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantNotificationsViewModel =
                new ViewModelProvider(this).get(RestaurantNotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications_restaurant, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstNotification = new ArrayList<>();

        mDatabase.child("Notifications").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> notifications = task.getResult().getChildren();

                    for (DataSnapshot notification : notifications) {


                        lstNotification.add(new Notification(
                                String.valueOf(notification.getKey()),
                                String.valueOf(notification.child("user_id").getValue()),
                                String.valueOf(notification.child("date").getValue()),
                                String.valueOf(notification.child("time").getValue()),
                                String.valueOf(notification.child("state").getValue()),
                                String.valueOf(notification.child("content").getValue())

                        ));

                        Collections.sort(lstNotification, new Comparator<Notification>() {
                            @SuppressLint("SimpleDateFormat")
                            final
                            DateFormat f = new SimpleDateFormat("hh:mm");
                            @Override
                            public int compare(Notification lhs, Notification rhs) {
                                try {
                                    return Objects.requireNonNull(f.parse(rhs.getTime())).compareTo(f.parse(lhs.getTime()));
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        Collections.sort(lstNotification, new Comparator<Notification>() {
                            @SuppressLint("SimpleDateFormat")
                            final
                            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                            @Override
                            public int compare(Notification lhs, Notification rhs) {
                                try {
                                    return Objects.requireNonNull(f.parse(rhs.getDate())).compareTo(f.parse(lhs.getDate()));
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_restaurant_notifications);
                    myAdapter = new RecyclerViewAdapter_restaurant_notification(getActivity(), RestaurantNotificationsFragment.this, lstNotification);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }
        });

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_notification_restaurant);

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
        lstNotification = new ArrayList<>();

        mDatabase.child("Notifications").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> notifications = task.getResult().getChildren();

                    for (DataSnapshot notification : notifications) {


                        lstNotification.add(new Notification(
                                String.valueOf(notification.getKey()),
                                String.valueOf(notification.child("user_id").getValue()),
                                String.valueOf(notification.child("date").getValue()),
                                String.valueOf(notification.child("time").getValue()),
                                String.valueOf(notification.child("state").getValue()),
                                String.valueOf(notification.child("content").getValue())

                        ));

                        Collections.sort(lstNotification, new Comparator<Notification>() {
                            @SuppressLint("SimpleDateFormat")
                            final
                            DateFormat f = new SimpleDateFormat("hh:mm");
                            @Override
                            public int compare(Notification lhs, Notification rhs) {
                                try {
                                    return Objects.requireNonNull(f.parse(rhs.getTime())).compareTo(f.parse(lhs.getTime()));
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                        Collections.sort(lstNotification, new Comparator<Notification>() {
                            @SuppressLint("SimpleDateFormat")
                            final
                            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                            @Override
                            public int compare(Notification lhs, Notification rhs) {
                                try {
                                    return Objects.requireNonNull(f.parse(rhs.getDate())).compareTo(f.parse(lhs.getDate()));
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        });

                    }


                    myAdapter = new RecyclerViewAdapter_restaurant_notification(getActivity(), RestaurantNotificationsFragment.this, lstNotification);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }
        });
    }
}

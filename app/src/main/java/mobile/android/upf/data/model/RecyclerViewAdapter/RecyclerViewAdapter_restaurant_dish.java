package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.RestaurantViewElementActivity;
import mobile.android.upf.data.model.Dish;

public class RecyclerViewAdapter_restaurant_dish extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_dish.MyViewHolder> {

    private Context mContext;
    private ViewGroup parent;
    private List<Dish> mData;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_restaurant_dish(Context mContext, List<Dish> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter_restaurant_dish.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        view = mInflater.inflate(R.layout.cardview_item_dish, parent,false);
        return new RecyclerViewAdapter_restaurant_dish.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_dish_name.setText(mData.get(position).getName());
        holder.tv_dish_description.setText(mData.get(position).getDescription());
        holder.tv_dish_price.setText(String.valueOf(mData.get(position).getPrice()));

        holder.tv_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        // set message, title, and icon
                        .setTitle(R.string.confirm_delete)
                        .setMessage(R.string.confirm_delete)
                        .setIcon(R.drawable.ic_baseline_delete_24_black)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                String toDeleteId = mData.get(position).getId();
                                Log.d("Dish to delete id: ", toDeleteId);

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mDatabase.child("Dishes").child(toDeleteId).setValue(null);

                                        ((RestaurantViewElementActivity)mContext).updateRecycler();

                                        Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("firebase", "Error while removing data from db");
                                    }
                                });


                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .create();
                myQuittingDialogBox.show();

                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#6200EE"));

            }
        });

        holder.tv_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog_styler_dish_modify, parent, false);
                final EditText name = (EditText) viewInflated.findViewById(R.id.modify_dish_name_txedit);
                name.setText(mData.get(position).getName());
                final EditText price = (EditText) viewInflated.findViewById(R.id.modify_dish_price_txedit);
                price.setText(String.valueOf(mData.get(position).getPrice()));
                final EditText description = (EditText) viewInflated.findViewById(R.id.modify_dish_description_txedit);
                description.setText(mData.get(position).getDescription());


                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        // set message, title, and icon
                        .setTitle(R.string.edit)
                        .setView(viewInflated)
                        .setIcon(R.drawable.ic_baseline_edit_24_black)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                String toEditId = mData.get(position).getId();
                                Log.d("Dish to edit id: ", toEditId);

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String update_name, update_description;
                                        double update_price;

                                        if(!name.getText().toString().equals("")){
                                            update_name = name.getText().toString();
                                        }
                                        else {
                                            update_name = mData.get(position).getName();
                                        }

                                        if(!description.getText().toString().equals("")){
                                            update_description = description.getText().toString();
                                        }
                                        else {
                                            update_description = mData.get(position).getDescription();
                                        }

                                        if(!price.getText().toString().equals("")){
                                            update_price = Double.parseDouble(price.getText().toString());
                                        }
                                        else {
                                            update_price = mData.get(position).getPrice();
                                        }

                                        Dish update = new Dish(toEditId, update_name, update_description,
                                                mData.get(position).getRestaurant_id(), update_price, mData.get(position).getNumber());
                                        mDatabase.child("Dishes").child(toEditId).setValue(update);

                                        ((RestaurantViewElementActivity)mContext).updateRecycler();

                                        Toast.makeText(mContext, R.string.update, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("firebase", "Error while editing data from db");
                                    }
                                });


                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .create();



                myQuittingDialogBox.show();

                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#6200EE"));

            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_dish_name,tv_dish_description,tv_dish_price;
        CardView tv_dish_card;
        Button tv_delete_btn, tv_edit_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_dish_card = (CardView) itemView.findViewById(R.id.dish_card);

            tv_dish_name = (TextView) itemView.findViewById(R.id.dish_name_element);
            tv_dish_description = (TextView) itemView.findViewById(R.id.dish_description_element);
            tv_dish_price = (TextView) itemView.findViewById(R.id.dish_price_element);

            tv_delete_btn = (Button) itemView.findViewById(R.id.delete_dish_card_btn);
            tv_edit_btn = (Button) itemView.findViewById(R.id.edit_dish_card_btn);

        }
    }
}

package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Notification;

public class RecyclerViewAdapter_restaurant_notification extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_notification.MyViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Notification> mData;
    private DatabaseReference mDatabase;

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    public RecyclerViewAdapter_restaurant_notification(Context mContext, Fragment mFragment, List<Notification> mData) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_restaurant_notification.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        this.parent = parent;
        view = mInflater.inflate(R.layout.cardview_item_notification, parent,false);
        return new RecyclerViewAdapter_restaurant_notification.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_notification_date.setText(mData.get(position).getDate());
        holder.tv_notification_message.setText(mData.get(position).getContent());
        holder.tv_notification_time.setText(mData.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_notification_message, tv_notification_date, tv_notification_time;
        CardView tv_notification_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notification_card = (CardView) itemView.findViewById(R.id.notification_card);

            tv_notification_date = (TextView) itemView.findViewById(R.id.notification_date);
            tv_notification_time = (TextView) itemView.findViewById(R.id.notification_time);
            tv_notification_message = (TextView) itemView.findViewById(R.id.notification_message);


        }
    }


}

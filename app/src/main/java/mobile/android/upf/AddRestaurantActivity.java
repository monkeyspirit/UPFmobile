package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.data.model.User;

import static android.widget.Toast.LENGTH_LONG;

public class AddRestaurantActivity extends AppCompatActivity {

    private Button add_restaurant_btn;
    private EditText restaurant_name, restaurant_phone, restaurant_address, restaurant_emailAddress,
            restaurant_description, restaurant_city;
    private CircularImageView restaurant_pic;

    private ProgressBar progressBar;

    private static final String TAG_LOG = "AddRestaurantActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private Uri imageUri;

    private Restaurant restaurant;

    private boolean noImageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_add_restaurant);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        String restaurateur_id = mAuth.getCurrentUser().getUid();

        restaurant_name = (EditText) findViewById(R.id.restaurant_name);
        restaurant_phone = (EditText) findViewById(R.id.restaurant_phone);
        restaurant_address = (EditText) findViewById(R.id.restaurant_address);
        restaurant_emailAddress = (EditText) findViewById(R.id.restaurant_emailAddress);
        restaurant_description = (EditText) findViewById(R.id.restaurant_description);
        restaurant_city = (EditText) findViewById(R.id.restaurant_city);
        restaurant_pic = (CircularImageView) findViewById(R.id.restaurant_pic);

        add_restaurant_btn = (Button) findViewById(R.id.add_restaurant_btn);

        restaurant_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        add_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = restaurant_name.getText().toString().trim();
                String address = restaurant_address.getText().toString().trim();
                String city = restaurant_city.getText().toString().trim();
                String phone = restaurant_phone.getText().toString().trim();
                String email = restaurant_emailAddress.getText().toString().trim();
                String description = restaurant_description.getText().toString().trim();

                if (name.isEmpty()) {
                    restaurant_name.setError(getString(R.string.empty_name));
                    restaurant_name.requestFocus();
                    return;
                }
                if (description.isEmpty()) {
                    restaurant_description.setError(getString(R.string.empty_name));
                    restaurant_description.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    restaurant_emailAddress.setError(getString(R.string.empty_email));
                    restaurant_emailAddress.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    restaurant_emailAddress.setError(getString(R.string.invalid_email));
                    restaurant_emailAddress.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    restaurant_phone.setError(getString(R.string.empty_phone));
                    restaurant_phone.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    restaurant_phone.setError(getString(R.string.invalid_phone_number));
                    restaurant_phone.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    restaurant_address.setError(getString(R.string.empty_address));
                    restaurant_address.requestFocus();
                    return;
                }
                if (city.isEmpty()) {
                    restaurant_city.setError(getString(R.string.empty_city));
                    restaurant_city.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mDatabase.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data");
                        } else {
                            Iterable<DataSnapshot> admins_database = task.getResult().getChildren();
                            boolean allAdminBusy = true;
                            String admin_id = "";

                            for (DataSnapshot admin : admins_database) {
                                // Verifico che l'utente sia admin
                                if (Integer.parseInt(String.valueOf(admin.child("type").getValue())) == 4) {
                                    if (Integer.parseInt(String.valueOf(admin.child("work").getValue())) == 0) {
                                        mDatabase.child("Users").child(admin.getKey()).child("work").setValue(1);
                                        allAdminBusy = false;
                                        admin_id = admin.getKey();

                                    }
                                }
                            }

                            if (allAdminBusy) {
                                int counter = 0;
                                for (DataSnapshot admin : admins_database) {
                                    if (Integer.parseInt(String.valueOf(admin.child("type").getValue())) == 4) {
                                        mDatabase.child("Users").child(admin.getKey()).child("work").setValue(0);
                                    }
                                    if (counter == 0) {
                                        admin_id = admin.getKey();
                                    }
                                    counter++;
                                }
                            }

                            /**
                             * Status: 0 = non approvato; 1 = approvato
                             */
                            restaurant = new Restaurant(name, description, email, city,
                                    address, phone, restaurateur_id, admin_id, "", 0);
                            uploadPicture();

                            if (noImageLoaded) {
                                restaurant = new Restaurant(name, description, email,
                                        city, address, phone, restaurateur_id, admin_id,
                                        "https://firebasestorage.googleapis.com/v0/b/ultimatepizzafrisbee.appspot.com/o/ristorante.jpg?alt=media&token=4777a50d-1445-44e1-a62b-d895d9d7fb82", 0);
                            }

                            mDatabase.child("Restaurants").child(restaurant.getId()).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Intent returnIntent = new Intent();
                                        setResult(RESULT_OK, returnIntent);
                                        finish();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AddRestaurantActivity.this, getString(R.string.restaurant_registration_db_failed), LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo che apre una schermata per selezionare un'immagine dallo smartphone
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            restaurant_pic.setImageURI(imageUri);


            //uploadPicture();
        }
    }

    private String getFileExtension(Uri _imageUri) {

        if (_imageUri == null) {
            String urlImage = "https://firebasestorage.googleapis.com/v0/b/ultimatepizzafrisbee.appspot.com/o/ristorante.jpg?alt=media&token=8eb676a6-5658-4046-98f8-d21f6764ac20";
            _imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/ultimatepizzafrisbee.appspot.com/o/ristorante.jpg?alt=media&token=8eb676a6-5658-4046-98f8-d21f6764ac20");
            imageUri = _imageUri;
            noImageLoaded = true;

            Glide.with(AddRestaurantActivity.this)
                    .load(urlImage)
                    .into(restaurant_pic);
        }

        ContentResolver cr = AddRestaurantActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(_imageUri));
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(AddRestaurantActivity.this);
        pd.setTitle(getString(R.string.uploading_image));
        pd.show();

        StorageReference picRef = mStorageReference.child(restaurant.getId() + "." + getFileExtension(imageUri));
        //StorageReference picImagesRef = mStorageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

        picRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (!AddRestaurantActivity.this.isFinishing() && pd != null) {
                    pd.dismiss();
                }
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DatabaseReference restRef = mDatabase.child("Restaurants").child(restaurant.getId());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", uri.toString());

                        restRef.updateChildren(updates);

//                        Toast.makeText(AddRestaurantActivity.this, getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddRestaurantActivity.this, getString(R.string.upload_failed), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage(getString(R.string.progress) + (int) progressPercent + "%");
                    }
                });


    }

}
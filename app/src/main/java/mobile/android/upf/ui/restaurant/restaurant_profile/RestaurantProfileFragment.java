package mobile.android.upf.ui.restaurant.restaurant_profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

import mobile.android.upf.ClientHomepageActivity;
import mobile.android.upf.R;
import mobile.android.upf.RestaurantHomepageActivity;
import mobile.android.upf.ui.client.client_profile.ClientProfileViewModel;

import static android.app.Activity.RESULT_OK;

public class RestaurantProfileFragment extends Fragment {

    private RestaurantProfileViewModel restaurantProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private String userId;

    private TextView restaurateur_name, restaurateur_surname, restaurateur_phone, restaurateur_address, restaurateur_emailAddress;
    private EditText restaurateur_password_insert, restaurateur_passwordConfirm_insert, restaurateur_address_insert_change;
    private CircularImageView restaurateur_pic;
    private Uri imageUri;
    private Button restaurateur_change_password_button, restaurateur_change_address_button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantProfileViewModel =
                new ViewModelProvider(this).get(RestaurantProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_restaurant, container, false);

        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        restaurateur_name = (TextView) root.findViewById(R.id.restaurateur_name_textview);
        restaurateur_surname = (TextView) root.findViewById(R.id.restaurateur_surname_textview);
        restaurateur_phone = (TextView) root.findViewById(R.id.restaurateur_phone_textview);
        restaurateur_address = (TextView) root.findViewById(R.id.restaurateur_address_textview);
        restaurateur_emailAddress = (TextView) root.findViewById(R.id.restaurateur_emailAddress_textview);

        restaurateur_pic = (CircularImageView) root.findViewById(R.id.restaurateur_pic_imageview);

        restaurateur_password_insert = (EditText) root.findViewById(R.id.restaurateur_password_insert);
        restaurateur_passwordConfirm_insert = (EditText) root.findViewById(R.id.restaurateur_passwordConfirm_insert);
        restaurateur_address_insert_change = (EditText) root.findViewById(R.id.restaurateur_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    restaurateur_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    restaurateur_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    restaurateur_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    restaurateur_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    restaurateur_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));

                    String uriS = String.valueOf(task.getResult().child("imageUrl").getValue());

                    if (uriS != "") {
                        Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
                        Log.d("firebase", "Image Url: " + uri);
                        Glide.with(getContext()).load(uri).into(restaurateur_pic);
                    }
                }
            }
        });

        restaurateur_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        restaurateur_address_insert_change.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }
        });

        restaurateur_change_address_button = (Button) root.findViewById(R.id.restaurateur_change_address_button);
        restaurateur_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("address", restaurateur_address_insert_change.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                restaurateur_address_insert_change.setText("");
            }
        });

        restaurateur_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(restaurateur_password_insert, restaurateur_passwordConfirm_insert,restaurateur_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }
        });

        restaurateur_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(restaurateur_password_insert, restaurateur_passwordConfirm_insert,restaurateur_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }
        });

        restaurateur_change_password_button = (Button) root.findViewById(R.id.restaurateur_change_password_button);
        restaurateur_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", restaurateur_password_insert.getText().toString());

                userRef.updateChildren(updates);


                String newPassword = restaurateur_password_insert.getText().toString();

                currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("firebase", "User password updated.");
                        }
                        else {
                            Log.d("firebase", "User password NOT updated.");
                        }
                    }
                });
                //Clean up the edittext
                restaurateur_password_insert.setText("");
                restaurateur_passwordConfirm_insert.setText("");
            }
        });

        return root;
    }

    public void enableSubmitIfReady(EditText editText, Button button) {
        boolean isReady = editText.getText().toString().length() > 1;
        button.setEnabled(isReady);
    }

    public void enablePasswordSubmitIfReady(EditText password, EditText password_confirm, Button button) {
        boolean isReady = password.getText().toString().equals(password_confirm.getText().toString()) && password.getText().toString().length()>5;
        button.setEnabled(isReady);
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
            restaurateur_pic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private String getFileExtension(Uri _imageUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(_imageUri));
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle(getString(R.string.uploading_image));
        pd.show();

        StorageReference picRef = mStorageReference.child(userId + "." + getFileExtension(imageUri));
        //StorageReference picImagesRef = mStorageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

        picRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                //Snackbar.make(getActivity().findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DatabaseReference userRef = mDatabase.child("Users").child(userId);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", uri.toString());

                        userRef.updateChildren(updates);

                        // Reload information
                        updateImageView(userId);

                        Toast.makeText(getActivity(), getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.upload_failed), Toast.LENGTH_LONG).show();
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

    public void updateTextView(String userId){
        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    restaurateur_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    restaurateur_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    restaurateur_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    restaurateur_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    restaurateur_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });
    }
    public void updateImageView(String userId) {
        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting image data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
                    Log.d("firebase", "Image Url: " + uri);
                    Glide.with(getContext()).load(uri).into(restaurateur_pic);
                }
            }
        });
    }

}
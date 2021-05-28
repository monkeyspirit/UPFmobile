package mobile.android.upf.ui.admin.admin_profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

import mobile.android.upf.R;
import mobile.android.upf.ui.client.client_profile.ClientProfileViewModel;

import static android.app.Activity.RESULT_OK;

public class AdminProfileFragment extends Fragment {

    private AdminProfileViewModel adminProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private TextView admin_name, admin_surname, admin_phone, admin_city, admin_address,
            admin_emailAddress;
    private EditText admin_password_insert, admin_passwordConfirm_insert, admin_city_insert,
            admin_address_insert;
    private ImageView admin_pic;
    private Button admin_change_address_button, admin_change_password_button;
    private String userId;
    private Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminProfileViewModel =
                new ViewModelProvider(this).get(AdminProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        admin_name = (TextView) root.findViewById(R.id.admin_name_textview);
        admin_surname = (TextView) root.findViewById(R.id.admin_surname_textview);
        admin_phone = (TextView) root.findViewById(R.id.admin_phone_textview);
        admin_city = (TextView) root.findViewById(R.id.admin_city_textview);
        admin_address = (TextView) root.findViewById(R.id.admin_address_textview);
        admin_emailAddress = (TextView) root.findViewById(R.id.admin_emailAddress_textview);

        admin_pic = (ImageView) root.findViewById(R.id.admin_pic_imageview);

        admin_password_insert = (EditText) root.findViewById(R.id.admin_password_insert);
        admin_passwordConfirm_insert = (EditText) root.findViewById(R.id.admin_passwordConfirm_insert);
        admin_city_insert = (EditText) root.findViewById(R.id.admin_city_insert);
        admin_address_insert = (EditText) root.findViewById(R.id.admin_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    admin_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    admin_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    admin_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    admin_city.setText(String.valueOf(task.getResult().child("city").getValue()));
                    admin_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    admin_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });

        admin_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        admin_city_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(admin_city_insert, admin_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(admin_city_insert, admin_change_address_button);
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSubmitIfReady(admin_city_insert, admin_change_address_button);
            }
        });

        admin_address_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }
        });

        admin_change_address_button = (Button) root.findViewById(R.id.admin_change_address_button);
        admin_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("city", admin_city_insert.getText().toString());
                updates.put("address", admin_address_insert.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                admin_city_insert.setText("");
                admin_address_insert.setText("");
            }
        });

        admin_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(admin_password_insert, admin_passwordConfirm_insert,admin_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }
        });

        admin_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(admin_password_insert, admin_passwordConfirm_insert,admin_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }
        });

        admin_change_password_button = (Button) root.findViewById(R.id.admin_change_password_button);
        admin_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", admin_password_insert.getText().toString());

                userRef.updateChildren(updates);


                String newPassword = admin_password_insert.getText().toString();

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
                admin_password_insert.setText("");
                admin_passwordConfirm_insert.setText("");
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
            admin_pic.setImageURI(imageUri);
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
                    Glide.with(getContext()).load(uri).into(admin_pic);
                }
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
                    admin_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    admin_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    admin_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    admin_city.setText(String.valueOf(task.getResult().child("city").getValue()));
                    admin_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    admin_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });
    }
}
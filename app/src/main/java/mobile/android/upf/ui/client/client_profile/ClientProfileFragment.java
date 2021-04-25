package mobile.android.upf.ui.client.client_profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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

import static android.app.Activity.RESULT_OK;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ClientProfileFragment extends Fragment {

    private ClientProfileViewModel clientProfileViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private String userId;

    private EditText client_address_insert, client_passwordConfirm_insert, client_password_insert;
    private TextView client_name, client_surname, client_phone, client_address, client_email;
    private Button client_change_address_button, client_change_password_button;
    private ImageView client_pic;
    private Uri imageUri;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientProfileViewModel =
                new ViewModelProvider(this).get(ClientProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_client, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        client_name = (TextView) root.findViewById(R.id.client_name_textview);
        client_surname = (TextView) root.findViewById(R.id.client_surname_textview);
        client_phone = (TextView) root.findViewById(R.id.client_phone_textview);
        client_address = (TextView) root.findViewById(R.id.client_address_textview);
        client_email = (TextView) root.findViewById(R.id.client_emailAddress_textview);

        client_pic = (ImageView) root.findViewById(R.id.client_pic_imageview);

        client_password_insert = (EditText) root.findViewById(R.id.client_password_insert);
        client_passwordConfirm_insert = (EditText) root.findViewById(R.id.client_passwordConfirm_insert);
        client_address_insert = (EditText) root.findViewById(R.id.client_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    client_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    client_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    client_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    client_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    client_email.setText(String.valueOf(task.getResult().child("email").getValue()));

                    String uriS = String.valueOf(task.getResult().child("imageUrl").getValue());

                    if (uriS != "") {
                        Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
                        Log.d("firebase", "Image Url: " + uri);
                        Glide.with(getContext()).load(uri).into(client_pic);
                    }
                }
            }
        });

        client_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        client_address_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }
        });

        client_change_address_button = (Button) root.findViewById(R.id.client_change_address_button);
        client_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("address", client_address_insert.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                client_address_insert.setText("");
            }
        });

        client_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(client_password_insert, client_passwordConfirm_insert,client_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }
        });

        client_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(client_password_insert, client_passwordConfirm_insert,client_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }
        });

        client_change_password_button = (Button) root.findViewById(R.id.client_change_password_button);
        client_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", client_password_insert.getText().toString());

                userRef.updateChildren(updates);

                String newPassword = client_password_insert.getText().toString();

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
                client_password_insert.setText("");
                client_passwordConfirm_insert.setText("");
            }
        });

        return root;
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
            client_pic.setImageURI(imageUri);
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
        pd.setTitle("Uploading image...");
        pd.show();

        //final String randomKey = UUID.randomUUID().toString();

        //StorageReference picRef = mStorageReference.child(randomKey);
        StorageReference picRef = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        StorageReference picImagesRef = mStorageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));



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

                        Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_LONG).show();
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Uploading failed", Toast.LENGTH_LONG).show();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercent + "%");
            }
        });

    }

    public void enableSubmitIfReady(EditText editText, Button button) {
        boolean isReady = editText.getText().toString().length() > 1;
        button.setEnabled(isReady);
    }

    public void enablePasswordSubmitIfReady(EditText password, EditText password_confirm, Button button) {
        boolean isReady = password.getText().toString().equals(password_confirm.getText().toString()) && password.getText().toString().length()>5;
        button.setEnabled(isReady);
    }

    public void updateTextView(String userId) {
        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    client_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    client_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    client_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    client_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    client_email.setText(String.valueOf(task.getResult().child("email").getValue()));
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
                    Glide.with(getContext()).load(uri).into(client_pic);
                }
            }
        });
    }
}
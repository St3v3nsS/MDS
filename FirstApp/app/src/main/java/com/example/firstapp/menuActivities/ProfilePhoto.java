package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.activities.Navigation;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.responses.PhotoResponse;
import com.example.firstapp.services.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfilePhoto extends Fragment {
    // This class handles the profile photo changing by requesting either the camera of the files
    View rootView;
    ImageView imageView;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    CardView cardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_profile_photo, container, false);
        cardView = (CardView) rootView.findViewById(R.id.upload_cardview);

        imageView = (ImageView) rootView.findViewById(R.id.profile_image);
        getActivity().setTitle("Add profile photo");

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.import_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return rootView;
    }

    private void selectImage(){
        // handle the selection
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Camera")){
                    // if the camera is chosen, start making photo
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
                else if(options[which].equals("Gallery")){
                    // go to stored photos and choose one
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
                else{
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            // if the photo was selected --> add it in the spot available then upload it to the server
            final Bitmap bitmap;
            if(requestCode == REQUEST_CAMERA){

                Bundle bundle = data.getExtras();

                bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
                cardView.setVisibility(View.VISIBLE);
                uploadImage(bitmap);

            }
            else if(requestCode == SELECT_FILE){

                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
                cardView.setVisibility(View.VISIBLE);
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    uploadImage(bitmap);
                }catch (IOException ex){
                    ex.printStackTrace();
                }

            }


        }
    }


    private void uploadImage(Bitmap bitmap) {
        cardView.setOnClickListener(v -> {
            String filename = "currentImageToUpload";
            // uploading the photo by sending a multipart call to the server
            File f = new File(getContext().getCacheDir(), filename);
            try {


                f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapData);
                fos.flush();
                fos.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", f.getName(), reqFile);

                Api service = RetrofitClient.createService(Api.class);
                Call<ResponseBody> call = service.uploadPhoto(body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(getContext(),"Upload success!", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.avatar));
                            cardView.setVisibility(View.INVISIBLE);

                            // update the photo

                            NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
                            View header = navigationView.getHeaderView(0);
                            addProfilePhoto(header);


                        }else {
                            Toast.makeText(getContext(), "Some error occurred. Try again!", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        call.cancel();

                    }
                });

            }catch (IOException exp){
                exp.printStackTrace();
            }
        });

    }

    private void addProfilePhoto(View header) {
        ImageView profilePicture = (ImageView) header.findViewById(R.id.profile_picture);

        Api api = RetrofitClient.createService(Api.class);
        Call<PhotoResponse> call = api.getProfilePhoto();

        call.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                if (response.code() == 200){
                    if (response.body() != null){
                        String url = response.body().getUrl();
                        Uri imageUri = Uri.parse(url);
                        profilePicture.setImageURI(imageUri);
                    }else{
                        Toast.makeText(getContext(), "No image found", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getContext(), "Try again later!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable throwable) {
                Toast.makeText(getContext(), "Try again later!", Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }


}

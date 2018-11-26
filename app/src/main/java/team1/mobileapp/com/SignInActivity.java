package team1.mobileapp.com;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class SignInActivity extends AppCompatActivity {

    private final int PICK_IMAGE_1 = 1;
    private final int PERMISSION_READ_STORAGE = 1;

    ImageView imageViewProfileImage;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        imageViewProfileImage = findViewById(R.id.imageViewProfileImage);
        imageViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(SignInActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SignInActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.user_profile_image_pick)), PICK_IMAGE_1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_IMAGE_1):
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    Glide.with(getApplicationContext()).load(imageUri).into(imageViewProfileImage);

                } else {
                    Toast.makeText(this, getString(R.string.user_profile_image_back), Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
}

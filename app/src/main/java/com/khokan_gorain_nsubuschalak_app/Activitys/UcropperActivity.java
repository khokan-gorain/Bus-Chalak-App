package com.khokan_gorain_nsubuschalak_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.khokan_gorain_nsubuschalak_app.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class UcropperActivity extends AppCompatActivity {
    String sourceUri, destinationUri;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucropper);

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            sourceUri = intent.getStringExtra("SendImageData");
            uri = Uri.parse(sourceUri);
        } else {
            Toast.makeText(getApplicationContext(), "OOps! Something went wrong...", Toast.LENGTH_LONG).show();
        }

        destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();
        UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationUri)))
                .withOptions(options)
                //.withAspectRatio(16,9)
                .withMaxResultSize(200,200)
                .start(UcropperActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            Intent intent = new Intent();
            intent.putExtra("CROP",resultUri+"");
            setResult(101,intent);
            finish();

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }
    }
}
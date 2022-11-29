package com.example.Pdfreader;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Pdfreader.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView PagesNumber;
    ImageView PageView;
    String uriString = "";
    Bitmap mBitmap;

    PdfRenderer renderer;
    int total_pages = 0;
    int display_page = 0;
    public static final int PICK_FILE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button select = findViewById(R.id.Selectfile);
        Button next = findViewById(R.id.Next);
        Button prev = findViewById(R.id.Prev);
        Button TextButton = findViewById(R.id.ReadText);
        PagesNumber = findViewById(R.id.Pages);
        PageView = findViewById(R.id.Pageview);

        TextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractTextFromUri(getApplicationContext());
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_FILE);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to previous page
                if (display_page > 0) {
                    display_page--;
                    display(display_page);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to next page
                if (display_page < (total_pages - 1)) {
                    display_page++;
                    display(display_page);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
                            .openFileDescriptor(uri, "r");
                    renderer = new PdfRenderer(parcelFileDescriptor);
                    total_pages = renderer.getPageCount();
                    display_page = 0;
                    display(display_page);
                } catch (FileNotFoundException fnfe){

                } catch (IOException e){

                }
            }
        }
    }

    private void display(int _n) {
        if (renderer != null) {
            PdfRenderer.Page page = renderer.openPage(_n);
            mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            PageView.setImageBitmap(mBitmap);
            page.close();
            PagesNumber.setText((_n + 1) + "/" + total_pages);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (renderer != null){
            renderer.close();
        }
    }
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    PageView.setImageURI(uri);
                    uriString = uri.toString();
                }
            });

    public void  extractTextFromUri(Context context) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            InputImage image = InputImage.fromBitmap(mBitmap,0);
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    // Task completed successfully
                                    Intent intent = new Intent(getApplicationContext(), Result.class);
                                    intent.putExtra("message_key", visionText.getText());
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                        }
                                    });

    }
}

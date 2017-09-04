package madeinsummer.ratinglog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddPopup extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_popup);
        getSupportActionBar().hide();
        imageView = (ImageView) findViewById(R.id.imageView);

        // Dimension of the layout (needs to be fixed)
        DisplayMetrics dim = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dim);
        Double width = dim.widthPixels*0.8; // Set the width of screen 80% of the phone
        Double height = dim.heightPixels*0.5; // Set the height of screen 50% of the phone
        getWindow().setLayout(width.intValue(), height.intValue());
    }


    public void add_product(View view) {
        EditText productNameEditText = (EditText) findViewById(R.id.productNameEditText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        String name = productNameEditText.getText().toString();
        String rate = String.valueOf((int)ratingBar.getRating());

        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Please enter the name of the product", Toast.LENGTH_SHORT).show();
        } else if (name.length() > 50) {
            Toast.makeText(this, "Max limit of characters is 50, exceeded by " + String.valueOf(name.length() - 50), Toast.LENGTH_SHORT).show();
        } else if ((int)ratingBar.getRating() == 0) {
            Toast.makeText(this, "Please leave a rating", Toast.LENGTH_SHORT).show();
        } else {

            try {

                imageView = (ImageView) findViewById(R.id.imageView);
                if (MainActivity.pictureTaken == false) {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
                MainActivity.pictureTaken = false;
                byte[] image = imageViewToByte(imageView);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatted_calendar = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTimeString = formatted_calendar.format(calendar.getTime());

                MainActivity.products.add(name);
                MainActivity.ratings.add(rate);
                MainActivity.images.add(image);
                MainActivity.dates.add(currentDateTimeString);
                DataBaseFunctions.saveItemToDatabase(name, rate, currentDateTimeString, image);

                Toast.makeText(this, name + " is saved", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                MainActivity.pictureTaken = true;
            }
        }
    }


    public byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}

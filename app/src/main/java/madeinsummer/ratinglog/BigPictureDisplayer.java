package madeinsummer.ratinglog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;


public class BigPictureDisplayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture_displayer);
        getSupportActionBar().hide();

        int itemSelected = getIntent().getIntExtra("ITEM_SELECTED", 0);
        ImageView imageView = (ImageView) findViewById(R.id.bigImageView);

        byte[] imageArray = MainActivity.images.get(itemSelected);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        imageView.setImageBitmap(bitmap);

        // Dimension of the layout (needs to be fixed)
        DisplayMetrics dim = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dim);
        Double width = dim.widthPixels*0.6; // Set the width of screen 70% of the phone
        Double height = dim.heightPixels*0.6; // Set the height of screen 70% of the phone
        getWindow().setLayout(width.intValue(), height.intValue());
    }




    public void backToListView (View view) {
        finish();
    }
}

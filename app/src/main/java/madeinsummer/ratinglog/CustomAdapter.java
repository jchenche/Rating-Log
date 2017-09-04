package madeinsummer.ratinglog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


class CustomAdapter extends BaseAdapter {

    private Context context;
    int red = 0xFFA81B1B;
    int white = 0xFFFFFFFF;
    int black = 0xFF000000;
    int gold = 0xFFFFFF20;

    public CustomAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return MainActivity.products.size();
    }

    @Override
    public Object getItem(int i) {
        return MainActivity.products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.custom_layout, null);

        RatingBar rating = view.findViewById(R.id.productRatingBar);
        TextView product = view.findViewById(R.id.productTextView);
        ImageView imageView = view.findViewById(R.id.productImageView);

        product.setText(MainActivity.highlightText(MainActivity.currentSearchText, MainActivity.products.get(i)));
        rating.setRating(Integer.parseInt(MainActivity.ratings.get(i)));
        byte[] imageArray = MainActivity.images.get(i);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        imageView.setImageBitmap(bitmap);

        // Themes related codes
        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();

        if (MainActivity.savedTheme.equals("default")) {
            product.setTextColor(black);
            stars.getDrawable(2).setColorFilter(red, PorterDuff.Mode.SRC_ATOP);
        }
        else if (MainActivity.savedTheme.equals("night")) {
            product.setTextColor(white);
            stars.getDrawable(2).setColorFilter(gold, PorterDuff.Mode.SRC_ATOP);
        }
        else if (MainActivity.savedTheme.equals("red")) {
            product.setTextColor(white);
            stars.getDrawable(2).setColorFilter(red, PorterDuff.Mode.SRC_ATOP);
        }
        else if (MainActivity.savedTheme.equals("blue")) {
            product.setTextColor(black);
            stars.getDrawable(2).setColorFilter(red, PorterDuff.Mode.SRC_ATOP);
        }

        return view;
    }

}

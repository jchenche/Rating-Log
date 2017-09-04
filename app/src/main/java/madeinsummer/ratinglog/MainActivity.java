package madeinsummer.ratinglog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    // Content holder Variables (decided not the change the names)
    static public SQLiteDatabase myDatabase;
    static public ArrayList<String> products;
    static public ArrayList<String> ratings;
    static public ArrayList<byte []> images;
    static public ArrayList<String> dates; // Can be treated like an unique ID

    // State Variables
    private String sortingState;
    private boolean in_reverse;
    private boolean in_simple_view;
    public static String savedTheme;
    static public boolean pictureTaken;

    private SharedPreferences sharedPreferences;
    private RelativeLayout listLayout;
    private ListView listView;
    private CustomAdapter customAdapter;
    private SimpleCustomAdapter simpleCustomAdapter;
    private SearchView searchView;
    public static String currentSearchText;


    @Override
    public void onResume() {
        super.onResume();
        custom_filter(currentSearchText);
        updateListViewDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

//        MenuItem item = menu.findItem(R.id.reverse_list);
//        if (in_reverse == true) {
//            item.setChecked(true);
//        } else {
//            item.setChecked(false);
//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.go_to_add_popup:
                go_to_add_popup();
                return true;
            case R.id.plain:
                sharedPreferences.edit().putString("theme", "default").apply();
                setCurrentTheme();
                return true;
            case R.id.nightmode:
                sharedPreferences.edit().putString("theme", "night").apply();
                setCurrentTheme();
                return true;
            case R.id.red:
                sharedPreferences.edit().putString("theme", "red").apply();
                setCurrentTheme();
                return true;
            case R.id.blue:
                sharedPreferences.edit().putString("theme", "blue").apply();
                setCurrentTheme();
                return true;

            case R.id.view_mode:

                if (in_simple_view) {
                    listView.setAdapter(customAdapter);
                    in_simple_view = false;
                } else {
                    listView.setAdapter(simpleCustomAdapter);
                    in_simple_view = true;
                }
                return true;

            case R.id.sort_alp:
                sortingState = "product";
                sorting_activator();
                return true;
            case R.id.sort_rate:
                sortingState = "rate";
                sorting_activator();
                return true;
            case R.id.sort_date:
                sortingState = "date";
                sorting_activator();
                return true;
            case R.id.reverse_list:

                if (in_reverse == true) {
                    in_reverse = false;
                } else {
                    in_reverse = true;
                }
                item.setChecked(in_reverse);
                reverse_arrays();
                return true;

            case R.id.delete_all:
                deleteAllItems();
                return true;
            case R.id.about:
                go_to_about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Rating Log");

        MainActivity.myDatabase = this.openOrCreateDatabase("Products", MODE_PRIVATE, null);
        DataBaseFunctions.loadFromDatabase(); // Initialize products, ratings, images, dates and loads the table to them
        listLayout = (RelativeLayout) findViewById(R.id.listLayout);

        // States initializer
        sortingState = "date";
        in_reverse = false;
        in_simple_view = false;
        pictureTaken = false;

        // ListView Codes
        listView = (ListView)findViewById(R.id.listView);
        customAdapter = new CustomAdapter(this);
        simpleCustomAdapter = new SimpleCustomAdapter(this);
        if (!in_simple_view) {
            listView.setAdapter(customAdapter);
        } else {
            listView.setAdapter(simpleCustomAdapter);
        }
        enableClickListView();
        enableLongClickListView();

        // SearchView Codes
        searchView = (SearchView) findViewById(R.id.searchView);
        currentSearchText = "";
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchText = newText;
                custom_filter(currentSearchText);
                updateListViewDisplay();
                return false;
            }
        });

        // Theme code
        sharedPreferences = this.getSharedPreferences("com.example.chenjimmy369.ratingcatalog", Context.MODE_PRIVATE);
        setCurrentTheme();


    }


    public void setCurrentTheme() {

        savedTheme = sharedPreferences.getString("theme", "default");

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = searchView.findViewById(id);
        searchTextView.setTextSize(20);

        if (savedTheme.equals("default")) {
            listLayout.setBackgroundResource(R.drawable.white_paper);
            searchTextView.setTextColor(Color.BLACK);
        }
        else if (savedTheme.equals("night")) {
            listLayout.setBackgroundResource(R.drawable.black_plain);
            searchTextView.setTextColor(Color.WHITE);
        }
        else if (savedTheme.equals("red")) {
            listLayout.setBackgroundResource(R.drawable.simple_red);
            searchTextView.setTextColor(Color.WHITE);
        }
        else if (savedTheme.equals("blue")) {
            listLayout.setBackgroundResource(R.drawable.blue);
            searchTextView.setTextColor(Color.BLACK);
        }
        updateListViewDisplay();
    }


    public void enableClickListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                go_to_big_image(i);
            }
        });
    }


    public void enableLongClickListView() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int itemSelected = i;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete " + products.get(i) + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteItem(itemSelected);

                            }
                        }).setNegativeButton("Cancel", null).show();
                return true; // This makes sure the functionality comes from long clicking
            }
        });
    }


    public void deleteItem(int itemSelected) {
        MainActivity.products.remove(itemSelected);
        MainActivity.ratings.remove(itemSelected);
        MainActivity.images.remove(itemSelected);
        String datetime = MainActivity.dates.remove(itemSelected);
        updateListViewDisplay();
        DataBaseFunctions.deleteItemFromDatabase(datetime);
    }


    public void deleteAllItems() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setTitle("Are you sure you want to delete all items?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle("Please confirm")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        MainActivity.products.clear();
                                        MainActivity.ratings.clear();
                                        MainActivity.images.clear();
                                        MainActivity.dates.clear();
                                        updateListViewDisplay();
                                        DataBaseFunctions.dropTable();

                                    }
                                }).setNegativeButton("Cancel", null).show();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    // Filter the 4 global ArrayLists based on newText
    public void custom_filter(String newText) {

        DataBaseFunctions.loadFromDatabase(); // Initialize products, ratings, images, dates and loads the table to them
        sorting_activator();

        ArrayList<String> products_filtered = MainActivity.products;
        ArrayList<String> ratings_filtered = MainActivity.ratings;
        ArrayList<byte []> images_filtered = MainActivity.images;
        ArrayList<String> dates_filtered = MainActivity.dates;

        MainActivity.products = new ArrayList<>();
        MainActivity.ratings = new ArrayList<>();
        MainActivity.images = new ArrayList<>();
        MainActivity.dates  = new ArrayList<>();

        String target_string;
        Pattern p = Pattern.compile(newText.toLowerCase());
        Matcher m;

        for (int row = 0; row < products_filtered.size(); row++) {
            target_string = products_filtered.get(row).toLowerCase();
            m = p.matcher(target_string);
            if (m.find()) {

                MainActivity.products.add(products_filtered.get(row));
                MainActivity.ratings.add(ratings_filtered.get(row));
                MainActivity.images.add(images_filtered.get(row));
                MainActivity.dates.add(dates_filtered.get(row));

            }
        }

    }

    // Only this function calls the other sorting functions
    public void sorting_activator() {
        if (sortingState == "product") {
            sort_by(products);
            if (in_reverse) {
                reverse_arrays();
            }
        } else if (sortingState == "rate") {
            sort_by(ratings);
            if (!in_reverse) {
                reverse_arrays();
            }
        } else if (sortingState == "date") {
            sort_by(MainActivity.dates);
            if (in_reverse) {
                reverse_arrays();
            }
        } else {
            Toast.makeText(this, "Bug in sorting_activator!", Toast.LENGTH_SHORT).show();
        }
        updateListViewDisplay();
    }


    public void sort_by(ArrayList<String> arr) {
        MainActivity.products = sort_by_second_stage(arr, MainActivity.products);
        MainActivity.ratings = sort_by_second_stage(arr, MainActivity.ratings);
        MainActivity.images = sort_by_second_stage(arr, MainActivity.images);
        MainActivity.dates = sort_by_second_stage(arr, MainActivity.dates);
    }

    // Sort all arrays based on arr
    public <T> ArrayList<T> sort_by_second_stage(ArrayList<String> arr, ArrayList<T> target) {
        Map<String, T> hashMap = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            hashMap.put(arr.get(i) + String.valueOf(i), target.get(i)); // Key is only used for sorting purpose
        }

        Map<String, T> treeMap = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.toLowerCase().compareTo(s2.toLowerCase());
                    }
                });
        treeMap.putAll(hashMap);

        target = new ArrayList<>();
        for (Map.Entry<String, T> value : treeMap.entrySet()) {
            target.add(value.getValue());
        }
        return target;
    }


    public void reverse_arrays() {
        Collections.reverse(MainActivity.products);
        Collections.reverse(MainActivity.ratings);
        Collections.reverse(MainActivity.images);
        Collections.reverse(MainActivity.dates);
        updateListViewDisplay();
    }


    public void go_to_add_popup() {
        startActivity(new Intent(getApplicationContext(), AddPopup.class));
    }


    public void go_to_big_image(int position) {
        Intent intent = new Intent(getBaseContext(), BigPictureDisplayer.class);
        intent.putExtra("ITEM_SELECTED", position);
        startActivity(intent);
    }


    public void go_to_about() {
        startActivity(new Intent(getApplicationContext(), AboutPage.class));
    }


    public void updateListViewDisplay() {
        customAdapter.notifyDataSetChanged();
        simpleCustomAdapter.notifyDataSetChanged();
    }


    public static CharSequence highlightText(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {
            // Normalize search and originalText
            String normalizedText1 = Normalizer.normalize(search, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            String normalizedText2 = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            int start = normalizedText2.indexOf(normalizedText1);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + normalizedText1.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(Color.YELLOW), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText2.indexOf(normalizedText1, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }


}

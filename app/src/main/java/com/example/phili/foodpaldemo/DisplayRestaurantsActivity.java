package com.example.phili.foodpaldemo;

import com.example.phili.foodpaldemo.models.RestaurantItem;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayRestaurantsActivity extends AppCompatActivity {

    private TextView txt_display_restaurant_heading;
    private String city, cuisine;
    private ListView lv_restaurants;
    private ArrayList<RestaurantItem> restaurantList;
    private RestaurantAdapter adapter;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurants);

        lv_restaurants = findViewById(R.id.lv_restaurants);
        txt_display_restaurant_heading = findViewById(R.id.txt_display_restaurant_heading);

        restaurantList = new ArrayList<>();

        Intent intent = getIntent();
        city = intent.getStringExtra(DiscoverRestaurantActivity.MESSAGE_CITY);
        cuisine = intent.getStringExtra(DiscoverRestaurantActivity.MESSAGE_CUISINE);

        txt_display_restaurant_heading.setText(cuisine.concat(" cuisines in ".concat(city)));

        adapter = new RestaurantAdapter(this, R.layout.restaurant_list_item, restaurantList);
        lv_restaurants.setAdapter(adapter);


        runnable = new Runnable() {
            @Override
            public void run() {
                getRestaurantId();
            }
        };

        Thread thread = new Thread(null, runnable, "background");
        thread.start();
    }

    public void getRestaurantId() {
        final String urlCuisines = "https://developers.zomato.com/api/v2.1/cuisines?city_id=";
        final String entity_id = city.equals("Halifax") ? "3099" : "3095";
        String urlCuisinesWithCity = urlCuisines.concat(entity_id);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlCuisinesWithCity, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "First request: Success!",
                                Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = response.getJSONArray("cuisines");

                            String cuisine_id = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getJSONObject("cuisine")
                                        .getString("cuisine_name")
                                        .equals(cuisine)) {

                                    cuisine_id = String.valueOf(jsonArray.getJSONObject(i)
                                            .getJSONObject("cuisine")
                                            .getString("cuisine_id"));
                                    break;
                                }
                            }

                            if (!cuisine_id.equals("")) {
                                getRestaurants(entity_id, cuisine_id);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "No such kind of cuisine existed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException error) {
                            error.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "First request: Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        })
        {

            /**
             * Override the getHeaders method to pass the API key for GET method for Zamato
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("user-key", "4aecd32564a00af0b37e755ae360d4bc");
                return headers;
            }
        };

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void getRestaurants(String entity_id, String cuisine_id) {
        final String url = "https://developers.zomato.com/api/v2.1/search?entity_id=";
        String urlCuisines = url.concat(entity_id + "&entity_type=city&cuisines=" + cuisine_id);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlCuisines, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Second request: Success!",
                                Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = response.getJSONArray("restaurants");
                            restaurantList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i)
                                        .getJSONObject("restaurant");
                                String name = jsonObject.getString("name");
                                String rating = jsonObject.getJSONObject("user_rating")
                                        .getString("aggregate_rating");
                                restaurantList.add(new RestaurantItem(name, rating));
                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException error) {
                            error.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Second request: Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        })
        {

            /**
             * Override the getHeaders method to pass the API key for GET method for Zamato
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("user-key", "4aecd32564a00af0b37e755ae360d4bc");
                return headers;
            }
        };

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
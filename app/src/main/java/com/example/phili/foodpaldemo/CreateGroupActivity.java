package com.example.phili.foodpaldemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.phili.foodpaldemo.models.UserGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yiren on 2018-02-21.
 */

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener{

    //EditText on this page
    private EditText editTextgName;
    private EditText editTextTime;
    private EditText editTextRestaurant;

    private TextView textViewEmail;

    private Button btnCreate;

    // google place
    private ImageButton choosePlace;
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static LatLngBounds bounds = new LatLngBounds(new LatLng( 44.623740,-63.645071), new LatLng(44.684002, -63.557137));
    private TextView placeName;


    //Dialog for redirecting
    private ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, userDataReference;

    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_creat_group);

        //Initialize firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //If user is not logged on
        if (firebaseAuth.getCurrentUser() == null){
            //Finish the activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

        databaseReference = firebaseDatabase.getReference("groups");
        userDataReference = firebaseDatabase.getReference("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        editTextgName = findViewById(R.id.create_name);
        editTextTime = findViewById(R.id.create_time);
        choosePlace = findViewById(R.id.create_res);
        placeName = findViewById(R.id.place_name);

        textViewEmail = findViewById(R.id.textViewEmail);

        btnCreate = findViewById(R.id.btn_create);

        textViewEmail.setText("Welcome" + user.getEmail());

        btnCreate.setOnClickListener(this);
    }

    private void createGroup(){
        String groupName = editTextgName.getText().toString().trim();
        String mealTime = editTextTime.getText().toString().trim();

        String restaurantName = editTextRestaurant.getText().toString().trim();



        //String description =

        if (!TextUtils.isEmpty(groupName)) {
            String gId = databaseReference.push().getKey();
            //Construct a map to manage users and groups
            Map<String, Boolean> members = new HashMap<>();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uId = user.getUid();

            // add the group id to the user
            //  update the user's group info
            userDataReference.child(uId).child("joinedGroups").child(gId).setValue(true);

            //Put user to the current group
            members.put(uId,true);
            UserGroup userGroup = new UserGroup(gId,uId,groupName,mealTime,restaurantName,members);
            databaseReference.child(gId).setValue(userGroup);
            Log.i("test", "add group success");

            Toast.makeText(this, "create group success", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private void chooseRestaurant(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(bounds);

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(CreateGroupActivity.this, data);
                //PlaceEntity{id=ChIJ19nmdTAiWksRA1TUEF1FjHQ, placeTypes=[94, 1013, 34], locale=null, name=Dalhousie University,
                // address=6299 South St, Halifax, NS B3H 4R2, Canada, phoneNumber=+1 902-494-2211,
                // latlng=lat/lng: (44.636581199999995,-63.591655499999995), viewport=LatLngBounds{southwest=lat/lng: (44.63575445,-63.60206124999999),

                if(place != null){
                    placeName.setText(place.getName());

                }else {
                    Toast.makeText(this, "Please choose a restaurant", Toast.LENGTH_LONG).show();
                    return;
                }
                // northeast=lat/lng: (44.63906144999999,-63.58057544999999)}, websiteUri=http://www.dal.ca/, isPermanentlyClosed=false, priceLevel=-1}

            }
        }
    }



    @Override
    public void onClick(View view) {
        if (view == btnCreate) {
            createGroup();
        }

        if (view == choosePlace) {
            // start the place picker
            chooseRestaurant();


        }
    }
}

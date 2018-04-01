package com.example.phili.foodpaldemo.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.phili.foodpaldemo.CreateGroupActivity;
import com.example.phili.foodpaldemo.GroupHolder;
import com.example.phili.foodpaldemo.R;
import com.example.phili.foodpaldemo.models.UserGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends android.support.v4.app.Fragment implements RecyclerView.OnClickListener {

    private List<UserGroup> allGroups = new ArrayList<>();
    // widegs
    private ListView groupList;
    private FloatingActionButton createGroup;
    FirebaseRecyclerAdapter<UserGroup, GroupHolder> recyclerAdapter;
    private RecyclerView recyclerView;
    // firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    private String userID;

    private Query query;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get current user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupListView = inflater.inflate(R.layout.fragment_group_list, container, false);

        // get view elements
        recyclerView = groupListView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
        query = myRef;

        FirebaseRecyclerOptions<UserGroup> options =
                new FirebaseRecyclerOptions.Builder<UserGroup>()
                        .setQuery(query, UserGroup.class)
                        .build();
        // create adapter object
        recyclerAdapter = new FirebaseRecyclerAdapter<UserGroup, GroupHolder>(options) {

            @NonNull
            @Override
            public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // create a instance of viewHolder
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.groups_list_layout, parent, false);

                return new GroupHolder(view, getActivity());
            }

            @Override
            protected void onBindViewHolder(@NonNull GroupHolder holder, int position, @NonNull UserGroup model) {
                // bind data to widget
                holder.bind(model);
                String clickedKey =  recyclerAdapter.getRef(position).getKey();
                holder.setGroupID(clickedKey);


            }
        };

        // attach the adapter to recyclerView
        recyclerView.setAdapter(recyclerAdapter);
       // groupList = groupListView.findViewById(R.id.group_list);
        recyclerView.setOnClickListener(this);

        createGroup = groupListView.findViewById(R.id.create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
            }
        });

        return groupListView;
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }


    public void initRe


    @Override
    public void onClick(View v) {


    }
}

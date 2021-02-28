package com.example.instalearn.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instalearn.Model.Posts;
import com.example.instalearn.Model.Users;
import com.example.instalearn.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersFragment extends Fragment {

    DatabaseReference userRef;
    private FirebaseRecyclerOptions<Users> Options;
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter;
    RecyclerView userList;
    LinearLayoutManager linearLayoutManager;
    String strg_name,strg_email,strg_picture;
    public UsersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList=view.findViewById(R.id.users_list);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        userList.setHasFixedSize(true);
        userList.setLayoutManager(linearLayoutManager);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsers();
    }

    private void getUsers() {
        Options=new FirebaseRecyclerOptions.Builder<Users>().setQuery(userRef,Users.class).build();
        adapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

                strg_email=model.getEmail();
                strg_name=model.getName();
                strg_picture=model.getImage();

                holder.name.setText(strg_name);
                holder.email.setText(strg_email);
                Picasso.get().load(strg_picture).placeholder(R.drawable.profile_placeholder).into(holder.picture);


            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
                return new UsersViewHolder(view);
            }
        };
        adapter.startListening();
        userList.setAdapter(adapter);
    }


    private class UsersViewHolder extends RecyclerView.ViewHolder {
        CircleImageView picture;
        TextView name,email;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            picture=itemView.findViewById(R.id.users_picture);
            name=itemView.findViewById(R.id.users_name);
            email=itemView.findViewById(R.id.users_email);
        }
    }
}
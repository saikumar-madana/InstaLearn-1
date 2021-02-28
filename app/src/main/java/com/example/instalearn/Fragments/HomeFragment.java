package com.example.instalearn.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instalearn.Activities.InsertPostActivity;
import com.example.instalearn.Model.Posts;
import com.example.instalearn.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {


    FloatingActionButton addImageBtn;
    private Uri imageUri=null;
    private String uri;
    private FirebaseRecyclerOptions<Posts> Options;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter;
    FirebaseAuth mAuth;
    RecyclerView postList;
    DatabaseReference postRef,userRef;
    Query query;
    LinearLayoutManager linearLayoutManager;
    String post,userId,caption,current_user;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        addImageBtn=view.findViewById(R.id.add_post);
        postList=view.findViewById(R.id.post_list);
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        // random chat recycler view properties
        postList.setHasFixedSize(true);
        postList.setLayoutManager(linearLayoutManager);
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        current_user=mAuth.getCurrentUser().getUid();

        query=postRef;

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getPosts();
    }

    private void getPosts() {
        Options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(Options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {


                boolean isLiked=false,isSaved=false;

                post=model.getPost();
                userId=model.getUser();
                caption=model.getCaption();
                holder.postCaption.setText(caption);
                Picasso.get().load(post).placeholder(R.drawable.placeholder).into(holder.postImage);
                userRef.child(userId).addValueEventListener(new ValueEventListener() {

                    String postedBy,postedByImage;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postedBy= Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        if (snapshot.child("image").getValue().toString()!=null){
                            postedByImage=snapshot.child("image").getValue().toString();
                        }

                        holder.postUserName.setText(postedBy);
                        Picasso.get().load(postedByImage).into(holder.postUserPicture);
                        holder.postDetailName.setText(postedBy);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


              holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      String postKey=adapter.getRef(position).getKey();
                      HashMap<String,Object> keyMap=new HashMap<>();
                      keyMap.put("key",postKey);
                      userRef.child(current_user).child("Liked").push().setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    HashMap<String,Object> likeMap=new HashMap<>();
                                    keyMap.put("key",current_user);
                                    Log.d("POST_LOG",postKey);
                                    postRef.child(postKey).child("LikedBy").push().setValue(likeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                holder.postLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked,null));
                                                Toast.makeText(requireActivity(), "Post Liked", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(requireActivity(), "Couldn't like the post", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(requireActivity(), "Couldn't like the post", Toast.LENGTH_SHORT).show();
                                }
                          }
                      });
                  }
              });

              holder.postSaveBtn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      String postKey=adapter.getRef(position).getKey();
                      HashMap<String,Object> keyMap=new HashMap<>();
                      keyMap.put("key",postKey);
                      userRef.child(current_user).child("Saved").push().setValue(keyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    holder.postSaveBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_saved,null));
                                    Toast.makeText(requireActivity(), "Post Saved!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                          }
                      });
                  }
              });


            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);
                return new PostsViewHolder(view);
            }
        };
        adapter.startListening();
        postList.setAdapter(adapter);
    }

    private void openGallery() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            imageUri=data.getData();
            if (imageUri!=null){
                uri=imageUri.toString();
                Intent intent =new Intent(requireActivity(), InsertPostActivity.class);
                intent.putExtra("imageUri",uri);
                requireActivity().startActivity(intent);

            }

        }
        else{
            Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
        }


    }

    private static class PostsViewHolder extends RecyclerView.ViewHolder {
            CircleImageView postUserPicture;
            TextView postUserName,postLikesCount,postCaption,postDetailName;
            ImageButton postLikeBtn,postSaveBtn;
            ImageView postImage;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            postUserPicture= itemView.findViewById(R.id.post_user_image);
            postUserName=itemView.findViewById(R.id.post_user_name);
            postLikesCount=itemView.findViewById(R.id.post_like_count);
            postCaption=itemView.findViewById(R.id.post_detail_caption);
            postDetailName=itemView.findViewById(R.id.post_detail_user_name);
            postLikeBtn=itemView.findViewById(R.id.post_likeBtn);
            postSaveBtn=itemView.findViewById(R.id.post_saveBtn);
            postImage=itemView.findViewById(R.id.post_image);

        }
    }
}
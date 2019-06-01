package com.example.thebeast.smartadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fidel M Omolo on 5/5/2018.
 */

public class Forum_Adapter extends RecyclerView.Adapter<Forum_Adapter.ViewHolder>{


    List<Forum_Adapter_model>bloglist;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    String currentUserId;


    public Forum_Adapter(List<Forum_Adapter_model> bloglist) {
        //the constructor is receiving data from the list data structure in HomeFragment java class
        this.bloglist=bloglist;



    }

    @NonNull
    @Override
    public Forum_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the custom made Layout file for list items
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list,parent,false);
        context=parent.getContext();
        firestore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Forum_Adapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);//makes the recycler views not to be recycled
        //gets data stored in the bloglist List data structure, the getDescription model class is found in the model class

        final String blogpostid=bloglist.get(position).BlogPostIdString;//gets the blog post id
        String description_data=bloglist.get(position).getDescription();
        String image_url=bloglist.get(position).getImageUri();
        String thumb_uri=bloglist.get(position).getThumbUri();
        String title=bloglist.get(position).getTitle();
        final String user_id=bloglist.get(position).getUser_id();
         Long timestamp=bloglist.get(position).getTimestamp();



        holder.getDate(timestamp);


       //loads user details on the posted blog items

        firestore.collection("user_table").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    String Fname=task.getResult().getString("fname");
                    String Lname=task.getResult().getString("lname");
                    String image1=task.getResult().getString("thumburi");

                    holder.setUserData(image1,Fname,Lname);

                }else{




                    firestore.collection("Admin_table").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            if(task.getResult().exists()){

                                String Fname="Admin - "+task.getResult().getString("fname");
                                String Lname=task.getResult().getString("lname");
                                String image1=task.getResult().getString("thumburi");

                                holder.setUserData(image1,Fname,Lname);

                            }



                        }
                    });




                }






            }
        });











        //changes the color of the like button
        firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                //he has liked the post
                if(documentSnapshot.exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.like_btn.setImageDrawable(context.getDrawable(R.mipmap.like_btn));
                    }
                }else{

                    //he has disliked the post

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        holder.like_btn.setImageDrawable(context.getDrawable(R.mipmap.nolike_btn));
                    }

                }
            }
        });

        //updates the number of likes
        firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                if (!queryDocumentSnapshots.isEmpty()){

                    //if the Likes collection is not empty do the following

                    int number_of_likes=queryDocumentSnapshots.size();

                    holder.updateLike_Count(number_of_likes);

                }
                else {

                    //if the Likes collection if empty do the following
                    holder.updateLike_Count(0);

                }

            }
        });

        //updates the number of unlikes
        firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                if (!queryDocumentSnapshots.isEmpty()){

                    //if the Likes collection is not empty do the following

                    int number_of_unlikes=queryDocumentSnapshots.size();

                    holder.updateUnlike_Count(number_of_unlikes);

                }
                else {

                    //if the Likes collection if empty do the following
                    holder.updateUnlike_Count(0);

                }

            }
        });




        //changes the color of the unlike button
        firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override


            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                //he has liked the post

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                if(documentSnapshot.exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.unlike_btn.setImageDrawable(context.getDrawable(R.mipmap.unlike_icon_true));
                    }
                }else{

                    //he has disliked the post

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        holder.unlike_btn.setImageDrawable(context.getDrawable(R.mipmap.unlike_icon_false));
                    }

                }
            }
        });



        //delete or creates a timestamp in the likes table when the like button is clicked
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).
                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                             if (task.isSuccessful()) {
                                 if (!task.getResult().exists()) {

                                     //if the like does not exist do the following
                                     Map<String, Object> likeMap = new HashMap<>();
                                     likeMap.put("TimeStamp", FieldValue.serverTimestamp());

                                     firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).set(likeMap);

                                     holder.unlike_streamline(blogpostid);



                                 } else {

                                     firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).delete();


                                 }
                             }




                    }
                });


            }
        });



        //unlike button creates or deletes specific user content in the unlike comments table
        holder.unlike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).
                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {

                                //if the like does not exist do the following
                                Map<String, Object> likeMap = new HashMap<>();
                                likeMap.put("TimeStamp", FieldValue.serverTimestamp());
                                firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).set(likeMap);

                                holder.like_streamline(blogpostid);

                            } else {


                                firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).delete();


                            }
                        }




                    }
                });


            }
        });





        //comment count
        firestore.collection("Forum_Posts").document(blogpostid).
                collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {


            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                if (!queryDocumentSnapshots.isEmpty()){

                    //if the Likes collection is not empty do the following

                    int number_of_comments=queryDocumentSnapshots.size();

                    holder.updateComment_Count(number_of_comments);

                }
                else {

                    //if the Likes collection if empty do the following
                    holder.updateComment_Count(0);

                }
            }
        });


        //comment button click
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,comment.class);
                intent.putExtra("blogpostid",blogpostid);
                context.startActivity(intent);

            }
        });




        holder.setDescription(description_data,title);
        holder.setBlogImage(image_url,thumb_uri);

        holder.deletepost(blogpostid,position);


    }

    @Override
    public int getItemCount() {
        return bloglist.size();//number of items to be populated in the recycler view
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView description,title,like_count,comment_count,unlike_count,Blog_date;
        ImageView imageView,like_btn,comment_btn,unlike_btn,delete_post;
        TextView profile_name;
        CircleImageView profile_pic;

       /* ImageView blog_image_like_btn;
        TextView blog_image_like_count;*/




        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            like_btn=mView.findViewById(R.id.blog_like);

            delete_post=mView.findViewById(R.id.id_edit);

            unlike_btn=mView.findViewById(R.id.blog_unlike_btn12);

            like_count=mView.findViewById(R.id.blog_count);
            unlike_count=mView.findViewById(R.id.blog_unlike_count);

            comment_count=mView.findViewById(R.id.comment_count);
            comment_btn=mView.findViewById(R.id.comment_image_btn);

            Blog_date=mView.findViewById(R.id.blog_date);

        }

        public void deletepost(final String blogpostid, final int position) {



            delete_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Delete Post");
                    builder1.setMessage("Are you sure you want to delete the post?");
                    builder1.setCancelable(true);
                    builder1.setIcon(R.mipmap.delete_content);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    firestore.collection("Forum_Posts").document(blogpostid).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    bloglist.remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context, "Item has been deleted successfully", Toast.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                }
            });





        }


        public void updateLike_Count(int count){

            like_count=mView.findViewById(R.id.blog_count);
            like_count.setText(count+" Likes");

        }


        public  void setDescription(String descriptionText,String title1){
            title=mView.findViewById(R.id.blog_title);
            description=mView.findViewById(R.id.blog_description);

            title.setText(title1);
            description.setText(descriptionText);
        }


        public void setBlogImage(String downloadUri,String Thumburi){

            imageView=mView.findViewById(R.id.blog_post_image);

            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(downloadUri).thumbnail(
                    //loads the thumbnail if the image has not loaded first
                    Glide.with(context).load(Thumburi)
            ).into(imageView);


        }



       /* public void setTime(String date1){
            blogdate=mView.findViewById(R.id.blog_date);
            blogdate.setText(date1);

        }*/


        public void setUserData(String image,String Fname,String Lname){
            profile_name=mView.findViewById(R.id.blog_user_name);
            profile_pic=mView.findViewById(R.id.mypost_image);

            profile_name.setText(Fname+" "+Lname);

            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).setDefaultRequestOptions(placeHolder).load(image).into(profile_pic);



        }



        public void updateComment_Count(int number_of_comments) {
            comment_count=mView.findViewById(R.id.comment_count);
            comment_count.setText(number_of_comments+" Comments");


        }


        public void updateUnlike_Count(int count){

            unlike_count=mView.findViewById(R.id.blog_unlike_count);
            unlike_count.setText(count+" Likes");

        }


        public void unlike_streamline( final String blogpostid){
            firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {

                            firestore.collection("Forum_Posts").document(blogpostid).collection("Unlikes").document(currentUserId).delete();

                             }
                    }


                }
            });

        }

        public void like_streamline( final String blogpostid){
            firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {

                            firestore.collection("Forum_Posts").document(blogpostid).collection("Likes").document(currentUserId).delete();

                        }
                    }


                }
            });

        }

        public void getDate(Long timestamp) {
            Calendar calendar=Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(timestamp*1000);

            String date= DateFormat.getDateTimeInstance().format(calendar.getTime()).toString();
            //String date= DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString();



            Blog_date.setText(""+date);

        }


    }
}

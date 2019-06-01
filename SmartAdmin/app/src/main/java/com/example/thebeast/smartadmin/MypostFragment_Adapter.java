package com.example.thebeast.smartadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fidel M Omolo on 5/10/2018.
 */

public class MypostFragment_Adapter extends RecyclerView.Adapter<MypostFragment_Adapter.ViewHolder>
{

    List<Forum_Adapter_model> bloglist;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    String currentUserId;

    public MypostFragment_Adapter(List<Forum_Adapter_model> bloglist) {
        this.bloglist = bloglist;
    }


    @NonNull
    @Override
    public MypostFragment_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the custom made Layout file for list items
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mypostlist,parent,false);
        context=parent.getContext();
        firestore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MypostFragment_Adapter.ViewHolder holder, int position) {

        final String blogpostid=bloglist.get(position).BlogPostIdString;//gets the blog post id
        String thumb_uri=bloglist.get(position).getThumbUri();
        String title=bloglist.get(position).getTitle();



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


         holder.setImageTitle(thumb_uri,title);

         holder.deletepost(blogpostid,position);


    }

    @Override
    public int getItemCount() {
       return bloglist.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView title,like_count,unlike_count;
        CircleImageView profile_pic;
        ImageView delete_btn;





        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            delete_btn=mView.findViewById(R.id.mypost_delete_btn);





        }

        public void updateLike_Count(int count){

            like_count=mView.findViewById(R.id.mypost_like_count);
            like_count.setText(count+" Likes");

        }




        public void setImageTitle(String Thumburi,String title1){

              title=mView.findViewById(R.id.mypost_title);
              title.setText(title1);

            profile_pic=mView.findViewById(R.id.mypost_image);

            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(Thumburi).into(profile_pic);


        }



        public void updateUnlike_Count(int count){

            unlike_count=mView.findViewById(R.id.mypost_unlike_count);
            unlike_count.setText(count+" Likes");

        }


        public void deletepost(final String blogpostid, final int position) {

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        }
    }

}

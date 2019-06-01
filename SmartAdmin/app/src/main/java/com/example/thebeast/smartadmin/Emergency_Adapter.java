package com.example.thebeast.smartadmin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Emergency_Adapter extends RecyclerView.Adapter<Emergency_Adapter.ViewHolder> {

        List<EmergencyLines_model> bloglist;


        FirebaseFirestore firestore;
        FirebaseAuth mAuth;
        Context context;

public Emergency_Adapter(List<EmergencyLines_model> bloglist) {
        this.bloglist = bloglist;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the custom made Layout file for list items
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ambulancelist,parent,false);
        context=parent.getContext();
        firestore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        return new Emergency_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String blogpostid=bloglist.get(position).BlogPostIdString;//gets the blog post id
        final String thumb_uri=bloglist.get(position).getThumbUri();
        final String image_uri=bloglist.get(position).getImageUri();
        final String title=bloglist.get(position).getTitle();
        final String mobileno=bloglist.get(position).getMobileno();
        final String weburl=bloglist.get(position).getWeburl();
        final String email =bloglist.get(position).getEmail();
        final String latitude=bloglist.get(position).getLatitude();
        final String longitude=bloglist.get(position).getLongitude();


        holder.setImageTitle(thumb_uri,title);



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,Ambulance_Edit.class);
                intent.putExtra("image_uri",image_uri);
                intent.putExtra("title",title);
                intent.putExtra("thumb_uri",thumb_uri);
                intent.putExtra("mobileno",mobileno);
                intent.putExtra("weburl",weburl);
                intent.putExtra("email",email);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("docid",blogpostid);

                context.startActivity(intent);

            }
        });


        holder.deletepost(blogpostid,position);


    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView title;
        ImageView profile_pic;
        ImageView delete_btn;
        ConstraintLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            parentLayout=mView.findViewById(R.id.emergency_layout);
            delete_btn=mView.findViewById(R.id.ambulance_delete);


        }

        public void setImageTitle(String Thumburi,String title1){

            title=mView.findViewById(R.id.ambulance_name);
            title.setText(title1);
            profile_pic=mView.findViewById(R.id.profile_pic);
            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(Thumburi).into(profile_pic);

        }


        public void deletepost(final String blogpostid, final int position) {

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firestore.collection("EmergencyLines_Posts").document(blogpostid).delete()
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

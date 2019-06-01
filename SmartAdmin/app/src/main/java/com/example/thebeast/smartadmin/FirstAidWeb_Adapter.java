package com.example.thebeast.smartadmin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//import android.text.format.DateFormat;

public class FirstAidWeb_Adapter extends RecyclerView.Adapter<FirstAidWeb_Adapter.ViewHolder> {

    List<FirstAidWeb_model> bloglist;


    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    Context context;

    public FirstAidWeb_Adapter(List<FirstAidWeb_model> bloglist) {
        this.bloglist = bloglist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the custom made Layout file for list items
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.web_list,parent,false);
        context=parent.getContext();
        firestore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        return new FirstAidWeb_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String blogpostid=bloglist.get(position).BlogPostIdString;//gets the blog post id
        final String thumb_uri=bloglist.get(position).getThumbUri();
        final String image_uri=bloglist.get(position).getImageUri();
        final String title=bloglist.get(position).getTitle();
        final String weburl=bloglist.get(position).getWeburl();
        final Long time1=bloglist.get(position).getTimestamp();


      /*  holder.time.setText(""+time1);*/
        holder.setImageTitle(image_uri,title);

        holder.delete_weblist(blogpostid,position);


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,FirstAidWeb_View.class);
                intent.putExtra("weburl",weburl);
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView title,time;
        ImageView Weblist_image,Weblist_delete;
        ConstraintLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            parentLayout=mView.findViewById(R.id.weblist_layout);
            time=mView.findViewById(R.id.ambulance_delete);
        }

        public void setImageTitle(String Thumburi,String title1){
            title=mView.findViewById(R.id.weblist_name);
            title.setText(title1);
            Weblist_image=mView.findViewById(R.id.weblist_image);
            Weblist_delete=mView.findViewById(R.id.weblist_delete);
            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.addphoto_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(Thumburi).into(Weblist_image);

        }

        public void delete_weblist(final String blogpostid, final int position) {

            Weblist_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firestore.collection("FirstAidWeb_Posts").document(blogpostid).delete()
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

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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fidel M Omolo on 5/10/2018.
 */

public class AlertFragment_Adapter extends RecyclerView.Adapter<AlertFragment_Adapter.ViewHolder>
{

    List<Alert_Adapter_model> bloglist;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    String currentUserId;

    public AlertFragment_Adapter(List<Alert_Adapter_model> bloglist) {

        this.bloglist = bloglist;
    }


    @NonNull
    @Override
    public AlertFragment_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the custom made Layout file for list items
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.checkadminposts,parent,false);
        context=parent.getContext();
        firestore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlertFragment_Adapter.ViewHolder holder, int position) {

        final String blogpostid=bloglist.get(position).BlogPostIdString;//gets the blog post id
        String thumb_uri=bloglist.get(position).getThumbUri();
        String image_uri=bloglist.get(position).getImageUri();
        String title=bloglist.get(position).getTitle();
        long timestamp=bloglist.get(position).getTimestamp();

        holder.getDate(timestamp);



        firestore.collection("Alert_Posts").document(blogpostid).collection("Alert_Views").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                if (!queryDocumentSnapshots.isEmpty()){

                    //if the Likes collection is not empty do the following

                    int number_of_views=queryDocumentSnapshots.size();

                    holder.updateView_Count(number_of_views);

                }
                else {

                    //if the Likes collection if empty do the following
                    holder.updateView_Count(0);

                }

            }
        });





         holder.setImageTitle(image_uri,title);

         holder.deletepost(blogpostid,position);


    }

    @Override
    public int getItemCount() {
       return bloglist.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView title,like_count,delete,time_diasplay;
        ImageView profile_pic;
        ImageView delete_btn;





        public ViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            delete_btn=mView.findViewById(R.id.post_img);
            time_diasplay=mView.findViewById(R.id.id_time);



        }


        public void getDate(Long timestamp) {
            Calendar calendar=Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(timestamp*1000);

            String date= DateFormat.getDateTimeInstance().format(calendar.getTime()).toString();
            //String date= DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString();



            time_diasplay.setText(""+date);

        }

        public void updateView_Count(int count){
            like_count=mView.findViewById(R.id.view_txt);
            like_count.setText(count+" Views");
        }




        public void setImageTitle(String Thumburi,String title1){

              title=mView.findViewById(R.id.user_name);
              title.setText(title1);

              profile_pic=mView.findViewById(R.id.comment_userpic);

            RequestOptions placeHolder=new RequestOptions();
            placeHolder.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(Thumburi).into(profile_pic);


        }



        public void deletepost(final String blogpostid, final int position) {

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firestore.collection("Alert_Posts").document(blogpostid).delete()
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
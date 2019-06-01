package com.example.thebeast.smartadmin;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView blog_list_view;
    List<Forum_Adapter_model> bloglist; //list of type blog post model class.
    List<User_model_class> userlist; //list of type blog post model class.

    FirebaseFirestore firestore;
    Forum_Adapter forum_adapter;
    FirebaseAuth mAuth;
    DocumentSnapshot lastVisible;
    Boolean isFirstPageLoad=true;//true when data is loaded for the first time

    //Connection_Detector connection_detector;

    RecyclerView recyclerView;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        mAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();

        bloglist=new ArrayList<>();
        //userlist=new ArrayList<>();

        recyclerView=view.findViewById(R.id.blog_recycler_view);
        recyclerView.setHasFixedSize(true);
        //blog_list_view=view.findViewById(R.id.blog_recycler_view);
       // blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

       /* GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
        blog_list_view.setLayoutManager(gridLayoutManager);*/

       LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
       linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       recyclerView.setLayoutManager(linearLayoutManager);


        forum_adapter=new Forum_Adapter(bloglist);//initializing adapter
       // forum_adapter.setHasStableIds(true);

        recyclerView.setAdapter(forum_adapter);


        return view;//returns the inflated view
    }


    @Override
    public void onStart() {
        super.onStart();
        bloglist.clear();

        dataLoader();
       // forum_adapter.notifyDataSetChanged();//notify adapter when data set is changed


    }

    public void dataLoader(){
        Query firstQuery=firestore.collection("Forum_Posts").orderBy("timestamp", Query.Direction.DESCENDING);

        firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {


                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {



                        String PostId=documentChange.getDocument().getId();

                        final Forum_Adapter_model blogPost_model_class = documentChange.getDocument().toObject(Forum_Adapter_model.class).withId(PostId);



                        bloglist.add(blogPost_model_class);

                        forum_adapter.notifyDataSetChanged();//notify adapter when data set is changed


                    }

                }



                // blog_list_view.setHasFixedSize(true);


              /*  forum_adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);


                recyclerView.setItemAnimator(new ScaleInAnimator());
*/

            }
        });


    }






}

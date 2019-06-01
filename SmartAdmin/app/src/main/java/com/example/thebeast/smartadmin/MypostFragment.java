package com.example.thebeast.smartadmin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MypostFragment extends Fragment {
    RecyclerView blog_list_view;
    List<Forum_Adapter_model> bloglist; //list of type blog post model class.
    FirebaseFirestore firestore;
    MypostFragment_Adapter mypostFragment_adapter;
    FirebaseAuth mAuth;


    public MypostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_mypost, container, false);

        mAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();

        bloglist=new ArrayList<>();

        blog_list_view=view.findViewById(R.id.mypost_recycler_view);
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        mypostFragment_adapter=new MypostFragment_Adapter(bloglist);//initializing adapter
        blog_list_view.setAdapter(mypostFragment_adapter);



        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        bloglist.clear();
        dataLoader();

    }

    private void dataLoader() {

        Query firstQuery=firestore.collection("Forum_Posts").whereEqualTo("user_id",mAuth.getCurrentUser().getUid());

        firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        String PostId=documentChange.getDocument().getId();

                     Forum_Adapter_model forum_adapter_model= documentChange.getDocument().toObject(Forum_Adapter_model.class).withId(PostId);


                        bloglist.add(forum_adapter_model);
                        mypostFragment_adapter.notifyDataSetChanged();//notify adapter when data set is changed


                    }


                }


                }
        });

    }

}

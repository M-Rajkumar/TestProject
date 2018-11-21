package com.example.ids.testapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {


    public ArrayList<ContactsModel>alcontactsModels=null;
    ContactsModel contactsModel;
    Activity mActivity;
    Context context;

     public ContactListAdapter(Activity mActivity,ArrayList<ContactsModel>objContact){

         super();

         this.context = mActivity.getApplicationContext();
         this.mActivity = mActivity;
         this.alcontactsModels = objContact;
         contactsModel = new ContactsModel();
     }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_contact, parent, false);

        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder


        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.con_name.setText(""+alcontactsModels.get(position).getContactname());
        holder.con_email.setText(""+alcontactsModels.get(position).getContactemail());
        holder.con_mobile.setText(""+alcontactsModels.get(position).getContactmobile());


    }



    @Override
    public int getItemCount() {
         return this.alcontactsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView con_name,con_email,con_mobile;


        public MyViewHolder(View itemView) {
            super(itemView);
            con_name = (TextView) itemView.findViewById(R.id.name);
            con_email = (TextView) itemView.findViewById(R.id.email);
            con_mobile = (TextView) itemView.findViewById(R.id.mobile);

        }
    }
}

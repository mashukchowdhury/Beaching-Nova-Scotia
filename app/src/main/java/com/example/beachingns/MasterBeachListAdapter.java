package com.example.beachingns;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MasterBeachListAdapter extends RecyclerView.Adapter<MasterBeachListAdapter.ListItem> {
    private ArrayList<BeachItem> beaches;

    /**
     * constructor
     * @param beaches array of BeachItem objects to show on the list
     */
    public MasterBeachListAdapter(ArrayList<BeachItem> beaches) {

        this.beaches = beaches;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ListItem onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.beachitem, parent, false);

        return (new ListItem(v,beaches));
    }
    public void updateList(ArrayList<BeachItem> newBeaches) {
        beaches.clear();
        beaches.addAll(newBeaches);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ListItem listItem, int position) {
        // - replace the contents of the view with that element
        int pos = beaches.size() - (position + 1);//to reverse order
        String beachImageFileName = beaches.get(pos).getImageSource();
        listItem.beachName.setText(beaches.get(pos).getName());

        Log.d("beachesPosCap","CAP222;  " +beaches.get(pos).getcapacity()+";");
        if(beaches.get(pos).getcapacity()==null||beaches.get(pos).getcapacity()==""){

            listItem.beachItemCapacity.setText("Capacity:");
        }else {
            listItem.beachItemCapacity.setText(beaches.get(pos).getcapacity());
        }
        if(beaches.get(pos).getsandyOrRocky()==null||beaches.get(pos).getsandyOrRocky()==""){

            //  listItem.beachItemSandyOrRocky.setText("Sandy / Rocky:");
        }else {
            //  listItem.beachItemSandyOrRocky.setText("Sandy or Rocky: "+beaches.get(pos).getsandyOrRocky());
        }
        if(beaches.get(pos).getvisualWaterConditions()==null||beaches.get(pos).getvisualWaterConditions()==""){

            listItem.beachItemVisualWaterConditions.setText("Visual Water Conditions:");
        }else {
            listItem.beachItemVisualWaterConditions.setText(beaches.get(pos).getvisualWaterConditions());
        }
        String desc = beaches.get(pos).getDescription();
        listItem.setBeachImage(beachImageFileName);
    }

    public static class ListItem extends RecyclerView.ViewHolder {
        LinearLayout beachLayout;
        View mainView;
        TextView beachName;
        ImageView beachImage;
        TextView beachItemCapacity;
        TextView beachItemWheelChairRamp;
        TextView beachItemVisualWaterConditions;


        ArrayList<BeachItem> beachList;

        public ListItem(View listItemView, ArrayList<BeachItem> beaches) {
            super(listItemView);
            beachList =beaches;
            mainView = listItemView;
            beachName = listItemView.findViewById(R.id.BeachName);
            beachImage = listItemView.findViewById(R.id.BeachImage);
            beachItemCapacity = listItemView.findViewById(R.id.beachItemCapacityTextView);
            beachItemVisualWaterConditions = listItemView.findViewById(R.id.visualWaterConditionsTextView);
            beachLayout = listItemView.findViewById(R.id.beachItem);


            //make item clickable
            beachLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get position
                    gotToBeachMasterPage();
                }
            });
        }


        public void setBeachImage(String beachImageFileName){

            if(beachImageFileName.equals("")|| beachImageFileName == null){
                beachImageFileName ="default1.jpg";
            }
            beachImageFileName = beachImageFileName.replace('-','_');
            int fileExtension = beachImageFileName.indexOf('.');

            Log.d("SetImage"," file before parse "+beachImageFileName);
            if (fileExtension > -1) {
                beachImageFileName = beachImageFileName.substring(0, fileExtension);
            }
            String uri = "@drawable/"+beachImageFileName;
            Log.d("SetImage"," this is the file path: "+uri);
            int fileID =0;

            try{
                fileID = R.drawable.class.getField(beachImageFileName).getInt(null);
            }catch(IllegalAccessException e){
                Log.d("getImageIDError","Error getting image");
            }catch(NoSuchFieldException e2){
                Log.d("getImageIDError","no Icon found");
            }
            beachImage.setImageResource(fileID);
        }



        //  When click on a container go to > page with context (intent)
        private void gotToBeachMasterPage(){
            int pos = beachList.size() - (getAdapterPosition() + 1);//to reverse order
            BeachItem selectedBeach = beachList.get(pos);

            Intent intent = new Intent(mainView.getContext(), beachLandingPage.class);
            intent.putExtra("beachName",selectedBeach.getName());
            intent.putExtra("isFromPreferredList", true);
            mainView.getContext().startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {

        // if (beaches!=null)
        return beaches.size();
        //  else
        //     return 0;
    }
}

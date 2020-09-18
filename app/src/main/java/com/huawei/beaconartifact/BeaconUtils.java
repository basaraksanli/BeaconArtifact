package com.huawei.beaconartifact;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.beaconartifact.model.Artifact;
import com.huawei.beaconartifact.model.MockData;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.discovery.Distance;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.StatusCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BeaconUtils {
    private static BeaconUtils instance = new BeaconUtils();
    public MessageEngine messageEngine;
    public MessageHandler mMessageHandler;
    private static List<Artifact> downloadedArtifacts = new ArrayList<>();
    private static HashMap<String, Distance> artifactDistances = new HashMap<>();
    MockData mockData;

    public BeaconUtils() {
    }

    public static BeaconUtils getInstance() {
        return instance;
    }

    public void startScanning(Activity activity) {

        mockData= new MockData(activity);


        messageEngine = Nearby.getMessageEngine(activity);
        messageEngine.registerStatusCallback(
                new StatusCallback() {
                    @Override
                    public void onPermissionChanged(boolean isPermissionGranted) {
                        super.onPermissionChanged(isPermissionGranted);
                        Log.i("Beacon", "onPermissionChanged:" + isPermissionGranted);
                    }
                });



        mMessageHandler = new MessageHandler() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                doOnFound(message);
            }

            @Override
            public void onDistanceChanged(Message message, Distance distance) {
                super.onDistanceChanged(message, distance);
                doOnDistanceChanged(message, distance, activity);
            }
        };

        MessagePicker msgPicker = new MessagePicker.Builder().includeAllTypes().build();
        Policy policy = new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_INFINITE).build();
        GetOption getOption = new GetOption.Builder().setPicker(msgPicker).setPolicy(policy).build();

        Nearby.getMessageEngine(activity).get(mMessageHandler);

        Task<Void> task = messageEngine.get(mMessageHandler, getOption);
        task.addOnSuccessListener(
                aVoid -> Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show()).addOnFailureListener(
                e -> Log.e("Beacon", "Login failed:", e));

    }




    public void doOnFound(Message message){
        if (message == null) {
            return;
        }
        String type = message.getType();
        if (type == null) {
            return;
        }
        String artifactID;
        if(message.getType().equals("artifactKey")) {
            artifactID = new String(message.getContent());
            if(findDownloadedArtifact(artifactID)==null)
                downloadArtifactInformationByID(artifactID);
        }
    }
    private void downloadArtifactInformationByID(String artifactID) {
        downloadedArtifacts.add(mockData.getMockArtifact(artifactID));
    }

    public void doOnDistanceChanged(Message message, Distance distance, Activity activity){
        if (message == null) {
            return;
        }
        String type = message.getType();
        if (type == null) {
            return;
        }
        String messageContent = new String(message.getContent());
        Log.d("Beacon", "New Message:" + messageContent + " type:" + type + "Distance: "+ distance);
        if(type.equals("artifactKey"))
            operateOnDistanceChanged(messageContent, distance, activity);
    }

    private void operateOnDistanceChanged(String messageContent, Distance distance, Activity activity) {
        String artifactID = messageContent;
        artifactDistances.put(artifactID,distance);
        updateUI(activity);
    }

    private void updateUI(Activity activity){
        Map.Entry<String, Distance> closestArtifact = findClosest();

        TextView artifactName = activity.findViewById(R.id.artifactName);
        TextView description = activity.findViewById(R.id.artifactDescription);
        ImageView imageView = activity.findViewById(R.id.artifactImage);

        if(closestArtifact.getValue().getMeters() < 2)
        {
            Artifact closestInfo =findDownloadedArtifact(closestArtifact.getKey());
            if(closestInfo!=null)
            {
                artifactName.setText(closestInfo.getArtifactName());
                description.setText(closestInfo.getArtifactDescription());
                imageView.setImageDrawable(closestInfo.getArtifactImage());
            }
        }
        else{
            artifactName.setText(R.string.no_nearby_artifact);
            description.setText(R.string.no_nearby_artifact);
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }


    private Map.Entry<String, Distance> findClosest(){
        Map.Entry<String, Distance> closest = null;

        for (Map.Entry<String, Distance> entry : artifactDistances.entrySet())
        {
            if (closest == null || entry.getValue().compareTo(closest.getValue()) < 0)
            {
                closest = entry;
            }
        }
        return closest;
    }

    private Artifact findDownloadedArtifact(String ID){
        for(Artifact o : downloadedArtifacts)
        {
            if(o.getArtifactID().equals(ID))
                return o;
        }
        return null;
    }

    public void ungetMessageEngine() {
        if (messageEngine != null && mMessageHandler != null) {
            Log.i("Beacon", "unget");
            messageEngine.unget(mMessageHandler);
        }
    }
}


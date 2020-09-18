package com.huawei.beaconartifact.model;

import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

import com.huawei.beaconartifact.R;

import java.util.ArrayList;
import java.util.List;

public class MockData {
    List<Artifact> mockArtifacts;

    public MockData(Context context) {

        mockArtifacts = new ArrayList<>();

        mockArtifacts.add(new Artifact("1", "Mask of Agamemnon", "When the enthusiastic minded archaeologist Heinrich Schliemann discovered this golden mask at Mycenae in 1876, he had no doubt that it must be the death mask of Agamemnon himself, the king who led the Greeks in the Trojan war, only to be assassinated on his homecoming. Of course there's no proof of that, but it is one of the most compelling faces in art.", ResourcesCompat.getDrawable(context.getResources(), R.drawable.maskofagamennon, null)));
        mockArtifacts.add(new Artifact("2", "The Siren vase", "In Homer's Odyssey, one of the founding epics of Greek literature, Odysseus longs to hear the seductive yet dangerous song of the sirens that lure sailors to their deaths. So all his crew plug their ears, and Odysseus has himself lashed to the mast. This powerful painting captures the tension as Odysseus strains at his bonds, his whole body agonised, his head raised in rapt listening.", ResourcesCompat.getDrawable(context.getResources(), R.drawable.sirenvase, null)));
    }

    public List<Artifact> getMockList() {
        return mockArtifacts;
    }
    public Artifact getMockArtifact(String ID){
        for(Artifact artifact : mockArtifacts){
            if(ID.equals(artifact.getArtifactID()))
                return artifact;
        }
        return null;
    }
}

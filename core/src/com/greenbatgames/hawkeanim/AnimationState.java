package com.greenbatgames.hawkeanim;

/**
 * Created by Quiv on 2016-05-18.
 */
public enum AnimationState
{
    IDLE("Idle"),
    WALKING("Walking"),
    RUNNING("Running"),
    FALLING("Falling"),
    GLIDING("Gliding"),
    FLAPPING("Flap");

    private String label;

    private AnimationState(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }
}

package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Quiv on 28-05-2016.
 */
public class Utils
{
    private Utils() {}

    public static boolean almostEqualTo(float first, float second, float variance)
    {
        return Math.abs(first - second) < variance;
    }

    public static float getMassRatio(Body first, Body second, boolean getFirst)
    {
        float firstRatio = first.getMass() / (first.getMass() + second.getMass());
        float secondRatio = second.getMass() / (first.getMass() + second.getMass());

        if (getFirst)
            return firstRatio;
        return secondRatio;
    }

    public static boolean contactHasHawke(Contact contact)
    {
        Object
                a = contact.getFixtureA().getBody().getUserData(),
                b = contact.getFixtureB().getBody().getUserData();

        if ((a instanceof Hawke) || (b instanceof Hawke))
            return true;
        return false;
    }

    public static Hawke getHawkeContact(Contact contact) throws NullPointerException
    {
        Object
                a = contact.getFixtureA().getBody().getUserData(),
                b = contact.getFixtureB().getBody().getUserData();

        if (a instanceof Hawke)
            return (Hawke) a;
        else if (b instanceof Hawke)
            return (Hawke) b;
        else
            throw new NullPointerException();
    }

    public static Object getNonHawkeContact(Contact contact) throws NullPointerException
    {
        Object
                a = contact.getFixtureA().getBody().getUserData(),
                b = contact.getFixtureB().getBody().getUserData();

        if (a instanceof Hawke)
            return b;
        else if (b instanceof Hawke)
            return a;
        else
            throw new NullPointerException();
    }

    /**
     * Uses the user data from a contact to return the Fixture of the correct Fixture
     * in the collision - either A or B.
     *
     * User data is the user data set on the Fixture's Body, not on the Fixture itself
     *
     * @param contact
     * @param object
     * @return
     */
    public static Fixture getObjectFixture(Contact contact, Object object) throws NullPointerException
    {
        if (object == contact.getFixtureA().getBody().getUserData())
            return contact.getFixtureA();
        else if (object == contact.getFixtureB().getBody().getUserData())
            return contact.getFixtureB();
        else
            throw new NullPointerException();
    }
}

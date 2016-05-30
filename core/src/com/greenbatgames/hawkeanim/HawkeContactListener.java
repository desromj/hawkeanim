package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Quiv on 29-05-2016.
 */
public class HawkeContactListener implements ContactListener
{
    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        Object
                a = contact.getFixtureA().getBody().getUserData(),
                b = contact.getFixtureB().getBody().getUserData();

        if ((a instanceof Hawke && b instanceof Platform)
                || (a instanceof Platform && b instanceof Hawke))
        {
            Hawke hawke;
            Platform platform;

            if (a instanceof Hawke) {
                hawke = (Hawke) a;
                platform = (Platform) b;
            } else {
                hawke = (Hawke) b;
                platform = (Platform) a;
            }

            if (hawke.collisionDisabled() && platform.isOneWay()) {
                contact.setEnabled(false);
            } else if (platform.isOneWay()) {
                if (hawke.getFootYPosition() <= platform.top - Constants.PLATFORM_COLLISION_LEEWAY)
                    contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

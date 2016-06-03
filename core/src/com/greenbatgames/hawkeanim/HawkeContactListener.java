package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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

        if (Utils.contactHasHawke(contact)) {

            Hawke hawke = Utils.getHawkeContact(contact);
            Object other = Utils.getNonHawkeContact(contact);

            // Collision logic for Hawke landing on other physics objects.

            // Hawke can land on Physical Objects
            if (other instanceof PhysicalObject)
            {
                PhysicalObject physical = (PhysicalObject) other;

                /*
                    A collision has already happened, we just need to check:
                        - Hawke's position is above the other object's
                        - Hawke's x position is within the other's x + width
                  */
                Vector2 hawkePos = Utils.getObjectFixture(contact, hawke).getBody().getPosition();
                Vector2 otherPos = Utils.getObjectFixture(contact, physical).getBody().getPosition();

                boolean landed = false;

                if (hawkePos.y > otherPos.y)
                {
                    if ((hawkePos.x * Constants.PTM + Constants.PLATFORM_EDGE_LEEWAY > otherPos.x * Constants.PTM - physical.getWidth() / 2.0f)
                        && (hawkePos.x * Constants.PTM - Constants.PLATFORM_EDGE_LEEWAY < otherPos.x * Constants.PTM + physical.getWidth() / 2.0f))
                    {
                        landed = true;
                    }
                }

                if (landed)
                    hawke.land();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        if (Utils.contactHasHawke(contact)) {

            Hawke hawke = Utils.getHawkeContact(contact);
            Object other = Utils.getNonHawkeContact(contact);

            // Collision logic for one-way platforms
            if (other instanceof Platform) {

                Platform platform = (Platform) other;

                if (hawke.collisionDisabled() && platform.isOneWay()) {
                    contact.setEnabled(false);
                } else if (platform.isOneWay()) {
                    if (hawke.getFootYPosition() <= platform.top - Constants.PLATFORM_COLLISION_LEEWAY) {
                        contact.setEnabled(false);
                    }
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

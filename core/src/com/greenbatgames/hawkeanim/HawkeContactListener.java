package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
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
                    if (hawke.getFootYHeight() <= platform.top - Constants.PLATFORM_COLLISION_LEEWAY) {
                        contact.setEnabled(false);
                    }
                }
            }

            // Check logic for carrying Dynamic Physical Objects
            if (other instanceof PhysicalObject) {
                PhysicalObject physical = (PhysicalObject) other;

                if (Gdx.input.isKeyPressed(Input.Keys.X) && physical.getBody().getType() == BodyDef.BodyType.DynamicBody) {
                    hawke.carryObject(physical);
                } else {
                    hawke.dropCarriedObject();
                }
            }
        } else {

            Object first = contact.getFixtureA().getBody().getUserData();
            Object second = contact.getFixtureB().getBody().getUserData();

            if ((first instanceof Platform && second instanceof Box) || (first instanceof Box && second instanceof Platform))
            {
                Box box;
                Platform platform;

                if (first instanceof Box) {
                    box = (Box) first;
                    platform = (Platform) second;
                } else {
                    box = (Box) second;
                    platform = (Platform) first;
                }

                // Disable boxes colliding upwards with one-way platforms
                if (platform.isOneWay() && (box.getPosition().y < platform.top - Constants.PLATFORM_COLLISION_LEEWAY))
                {
                    Gdx.app.debug("Contact", "Box Bottom: " + box.getBody().getPosition());
                    Gdx.app.debug("Contact", "Platform Top: " + (platform.top - Constants.PLATFORM_COLLISION_LEEWAY));

                    contact.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

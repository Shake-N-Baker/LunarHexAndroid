package com.isb.lunarhex;

/**
 * The custom event class holds data for sending custom events.
 *
 * @author Ian Baker
 */
public class CustomEvent
{
    /**
     * Constants
     */
    public static final String NEW_CUSTOM_GAME = "newCustomGame";
    public static final String START_LEVEL = "startLevel";
    public static final String EXIT_GAME = "exitGame";
    public static final String TRANSITION_NEXT_LEVEL = "transitionNextLevel";

    /**
     * The custom event name
     */
    private String name;

    /**
     * Any additional data that goes along with the event
     */
    public String data;

    /**
     * Constructor for a custom event.
     *
     * @param   name - The name of the custom event
     */
    public CustomEvent(String name)
    {
        this.name = name;
    }

    /**
     * Constructor for a custom event.
     *
     * @param   name - The name of the custom event
     * @param   data - any additional data that goes along with the event
     */
    public CustomEvent(String name, String data)
    {
        this.name = name;
        this.data = data;
    }

    /**
     * Whether the custom event matches the specified type.
     *
     * @param   name - The type to compare this event to
     * @return  True or false whether the two match
     */
    public Boolean isType(String name)
    {
        return this.name.equals(name);
    }
}
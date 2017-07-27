package de.impelon.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * <p> Used for managing multiple objects of the same type. </p>
 * 
 * @author Impelon
 *
 */

public class ObjectManager<O> {
	
    private HashMap<Object, O> registered = new HashMap<Object, O>();
    
    /**
	 * <p> Registers the Object with the given ID. </p>
	 * 
	 * @param ID identification for the Object (key)
	 * @param object Object to register
	 * @return {@linkplain Map#put(Object, Object)}
	 */
    public O register(Object ID, O object) {
        return this.registered.put(ID, object);
    }
    
    /**
	 * <p> Unregisters the Object with the given ID. </p>
	 * 
	 * @param ID identification of the Object (key)
	 * @return {@linkplain Map#remove(Object)}
	 */
    public O unregister(Object ID) {
    	return this.registered.remove(ID);
    }
    
    /**
	 * <p> Unregisters all Objects. </p>
	 */
    public void unregisterAll() {
    	this.registered.clear();
    }
    
    /**
	 * <p> Gets the registered Object with the given ID. </p>
	 * 
	 * @param ID identification for the Object (key)
	 * @return {@linkplain Map#get(Object)}
	 */
    public O getRegistered(Object ID) {
        return this.registered.get(ID);
    }
    
    /**
	 * <p> Determines if there is a Object registered with the given key. </p>
	 * 
	 * @param ID identification for the Object (key)
	 * @return {@linkplain Map#containsKey(Object)}
	 */
    public boolean isRegisteredWithID(Object ID) {
        return this.registered.containsKey(ID);
    }
    
    /**
	 * <p> Determines if the given Object is registered. </p>
	 * 
	 * @param object Object to check if it is registered
	 * @return {@linkplain Map#containsValue(Object)}
	 */
    public boolean isRegistered(O object) {
        return this.registered.containsValue(object);
    }
    
}

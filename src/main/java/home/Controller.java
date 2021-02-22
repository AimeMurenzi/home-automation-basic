/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 22:17:41
 * @modify date 2021-01-26 22:17:41
 * @desc [description]
 */
package home;

import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("")
@PermitAll
public class Controller {
    @Inject
    private StateMap stateMap;
   
    @GET
    @PermitAll
    public String welcome() {
        return "HAB API: Welcome";
    }

    @GET
    @Path("speaker/on")
    public void speakerOn() { 
        stateMap.speakersOn();  
    }

    @GET
    @Path("speaker/off")
    public void speakerOff() {
        stateMap.speakersOff(); 
    }

    @GET
    @Path("lights/on")
    public void lightOn() { 
        stateMap.lightsOn();  
    }

    @GET
    @Path("lights/off")
    public void lightOff() {
        stateMap.lightsOff(); 
    }

    @GET
    @Path("state")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Boolean> state() {
        return stateMap.getState();
    }
}

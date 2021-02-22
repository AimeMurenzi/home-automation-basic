/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 22:18:02
 * @modify date 2021-01-26 22:18:02
 * @desc [description]
 */
package home;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
 
@Path("logout") 
public class Logout { 
    @Context ServletContext context; 
    @GET 
    public Response logout(@Context HttpServletRequest request) {
        Response.ResponseBuilder responseBuilder;        
        HttpSession httpSession = request.getSession(false);        
        if(httpSession==null) 
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
        else {
            httpSession.invalidate();
            responseBuilder = Response.status(Response.Status.OK);
        }  
        
        return responseBuilder.build();
    }
}

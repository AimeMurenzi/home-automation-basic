/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 22:18:08
 * @modify date 2021-01-26 22:18:08
 * @desc [description]
 */
package home;

import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
//TODO:DONE clean up
@Path("sse")
@Singleton
public class SseResource implements ISseResource {
    public SseResource() {
    }

    private Sse sse;
    private SseBroadcaster stateSseBroadcaster;
    private int evenId = 1;
    private int debugInitCallCounter = 0;
    private OutboundSseEvent outboundSseEvent;

    @Context
    public void setInit(Sse sse) {
        this.sse = sse;
        this.stateSseBroadcaster = sse.newBroadcaster();
        debugInitCallCounter++;
        System.out.println("debugInitCallCounter: " + debugInitCallCounter);
    }

    @GET
    @Path("subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sseEventSink) {

        this.stateSseBroadcaster.register(sseEventSink);
        if (outboundSseEvent != null)
            sseEventSink.send(outboundSseEvent);
    }

    public void broadcast(Map<String, Boolean> stateMap, String eventName) {
        evenId++;
        outboundSseEvent = sse.newEventBuilder().id(Integer.toString(evenId)).name(eventName)
                .mediaType(MediaType.APPLICATION_JSON_TYPE).data(Map.class, stateMap).reconnectDelay(3000)
                .comment("stateMap").build();
        stateSseBroadcaster.broadcast(outboundSseEvent);
    } 

}
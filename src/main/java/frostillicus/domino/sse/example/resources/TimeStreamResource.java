package frostillicus.domino.sse.example.resources;

import java.time.OffsetDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import frostillicus.domino.sse.example.RestEasyServlet;

@Path("time")
public class TimeStreamResource {
	public static final TimeStreamResource instance = new TimeStreamResource();
	
	private SseBroadcaster sseBroadcaster;
	private Sse sse;
	
	private BlockingQueue<String> messageQueue = new LinkedBlockingDeque<>(50);
	
	public TimeStreamResource() {
		// Spawn the threads at construction to avoid trouble with multiple setSse calls
		
		// Spawn a thread to pass messages to listeners
		RestEasyServlet.executor.submit(() -> {
			try {
				String message;
				while((message = messageQueue.take()) != null) {
					// The producer below may send a message before setSse is called the first time
					if(this.sseBroadcaster != null) {
						this.sseBroadcaster.broadcast(this.sse.newEvent("timeline", message)); //$NON-NLS-1$
					}
				}
			} catch(InterruptedException e) {
				// Then we're shutting down
			} finally {
				this.sseBroadcaster.close();
			}
		});
		
		// Spawn another thread to periodically generate new messages
		RestEasyServlet.executor.submit(() -> {
			try {
				while(true) {
					String eventContent = "- At the tone, the Domino time will be " + OffsetDateTime.now();
					messageQueue.offer(eventContent);
					
					// Note: any sleeping should be short enough that it doesn't block HTTP restart
					TimeUnit.SECONDS.sleep(10);
				}
			} catch(InterruptedException e) {
				// Then we're shutting down
			}
		});
	}

	@Context
	public void setSse(Sse sse) {
		this.sse = sse;
		this.sseBroadcaster = sse.newBroadcaster();
	}
	
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void get(@Context SseEventSink sseEventSink) {
		this.sseBroadcaster.register(sseEventSink);
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMessage(String message) throws InterruptedException {
		messageQueue.offer(message);
		return "Received message";
	}
}

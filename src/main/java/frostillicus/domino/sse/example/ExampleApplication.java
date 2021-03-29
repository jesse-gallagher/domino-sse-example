package frostillicus.domino.sse.example;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Application;

import frostillicus.domino.sse.example.resources.TimeStreamResource;

public class ExampleApplication extends Application {
	@Override
	public Set<Object> getSingletons() {
		// Use a singleton of the resource since we want a shared queue
		return Stream.of(
			TimeStreamResource.instance
		).collect(Collectors.toSet());
	}
}

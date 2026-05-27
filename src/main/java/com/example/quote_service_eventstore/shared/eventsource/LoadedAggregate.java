package com.example.quote_service_eventstore.shared.eventsource;

public class LoadedAggregate<A> {

    private final A aggregate;
    private final long version;

    public LoadedAggregate(A aggregate, long version) {
        this.aggregate = aggregate;
        this.version = version;
    }

    public A getAggregate() {
        return aggregate;
    }

    public long getVersion() {
        return version;
    }
}

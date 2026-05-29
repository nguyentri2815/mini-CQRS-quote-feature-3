package com.example.quote_service_eventstore.readmodel.quote.search;

public final class QuoteSearchIndexNames {

    public static final String QUOTE_INDEX_ALIAS = "quote_index";
    public static final String QUOTE_INDEX_PREFIX = "quote_index_v";

    private QuoteSearchIndexNames() {
    }

    public static String physicalIndexName(long version) {
        return QUOTE_INDEX_PREFIX + version;
    }
}

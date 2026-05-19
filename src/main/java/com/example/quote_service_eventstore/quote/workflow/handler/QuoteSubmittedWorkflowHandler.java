import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.workflow.QuoteSyncWorkflow;
import org.springframework.stereotype.Component;

@Component
public class QuoteSubmittedWorkflowHandler implements DomainEventHandler<QuoteSubmittedEvent> {

    private final QuoteSyncWorkflow quoteSyncWorkflow;

    public QuoteSubmittedWorkflowHandler(QuoteSyncWorkflow quoteSyncWorkflow) {
        this.quoteSyncWorkflow = quoteSyncWorkflow;
    }

    @Override
    public Class<QuoteSubmittedEvent> eventType() {
        return QuoteSubmittedEvent.class;
    }

    @Override
    public void handle(QuoteSubmittedEvent event) {
        quoteSyncWorkflow.onQuoteSubmitted(event);
    }
}

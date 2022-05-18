package cs.hse.edoquebot.webhook.requestObjects;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class WebhookRequest {

    private String responseId;
    private String session;
    private QueryResult queryResult;
    private OriginalDetectIntentRequest originalDetectIntentRequest;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

    public OriginalDetectIntentRequest getOriginalDetectIntentRequest() {
        return originalDetectIntentRequest;
    }

    public void setOriginalDetectIntentRequest(OriginalDetectIntentRequest originalDetectIntentRequest) {
        this.originalDetectIntentRequest = originalDetectIntentRequest;
    }

}

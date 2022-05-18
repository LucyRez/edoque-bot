package cs.hse.edoquebot.webhook.requestObjects;



import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class QueryResult {

    private String queryText;
    private Parameters parameters;
    private Boolean allRequiredParamsPresent;
    private String fulfillmentText;
    private List<Object> fulfillmentMessages = null;
    private List<Object> outputContexts = null;
    private Intent intent;
    private Integer intentDetectionConfidence;
    private DiagnosticInfo diagnosticInfo;
    private String languageCode;

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public Boolean getAllRequiredParamsPresent() {
        return allRequiredParamsPresent;
    }

    public void setAllRequiredParamsPresent(Boolean allRequiredParamsPresent) {
        this.allRequiredParamsPresent = allRequiredParamsPresent;
    }

    public String getFulfillmentText() {
        return fulfillmentText;
    }

    public void setFulfillmentText(String fulfillmentText) {
        this.fulfillmentText = fulfillmentText;
    }

    public List<Object> getFulfillmentMessages() {
        return fulfillmentMessages;
    }

    public void setFulfillmentMessages(List<Object> fulfillmentMessages) {
        this.fulfillmentMessages = fulfillmentMessages;
    }

    public List<Object> getOutputContexts() {
        return outputContexts;
    }

    public void setOutputContexts(List<Object> outputContexts) {
        this.outputContexts = outputContexts;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Integer getIntentDetectionConfidence() {
        return intentDetectionConfidence;
    }

    public void setIntentDetectionConfidence(Integer intentDetectionConfidence) {
        this.intentDetectionConfidence = intentDetectionConfidence;
    }

    public DiagnosticInfo getDiagnosticInfo() {
        return diagnosticInfo;
    }

    public void setDiagnosticInfo(DiagnosticInfo diagnosticInfo) {
        this.diagnosticInfo = diagnosticInfo;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

}

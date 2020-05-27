package io.illusionbank.sec.antifraud.gateway.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Migration {
    private String name;
    private String host;
    private Integer port;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Criteria criteria;

    public boolean matchWithClientAndRequest(ClientInfo clientInfo, AnalyzeRequest request) {

        switch (this.criteria.getCriteria()) {
            case "client.employeer": return this.criteria.matchValue(clientInfo.isEmployeer());
            case "client.segment": return this.criteria.matchValue(clientInfo.getSegment());
            case "client.agency.state": return this.criteria.matchValue(clientInfo.getAgency().getState());
            case "client.agency.code": return this.criteria.matchValue(clientInfo.getAgency().getCode());
            case "client.agency.city": return this.criteria.matchValue(clientInfo.getAgency().getCity());
        }
        return  false;
    }
}

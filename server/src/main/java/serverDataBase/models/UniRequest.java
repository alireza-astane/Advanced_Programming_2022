package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.UniRequestResultType;
import logic.enums.UniRequestType;

@Entity
@Getter
@Setter
@Table(name = "UniRequest")
public class UniRequest {

    @Id
    @GeneratedValue
    @Column(name = "uniRequestId",nullable = false)
    int uniRequestId;

    @Column(name = "uniRequestType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    UniRequestType uniRequestType;

    @Column(name = "uniRequestTime",nullable = false)
    String uniRequestTime;

    @Column(name = "uniRequestResult")
    @Enumerated(EnumType.ORDINAL)
    UniRequestResultType uniRequestResult=UniRequestResultType.NOT_ANSWERED;

    @Column(name = "uniRequestContext",length = 4000)
    String uniRequestContext="";

    @Column(name = "uniRequesterId",nullable = false)
    int uniRequesterId;

    @Column(name = "uniRequestResponsibleId")
    int uniRequestResponsibleId;

    public int getUniRequestId() {
        return uniRequestId;
    }

    public void setUniRequestId(int uniRequestId) {
        this.uniRequestId = uniRequestId;
    }

    public UniRequestType getUniRequestType() {
        return uniRequestType;
    }

    public void setUniRequestType(UniRequestType uniRequestType) {
        this.uniRequestType = uniRequestType;
    }

    public String getUniRequestTime() {
        return uniRequestTime;
    }

    public void setUniRequestTime(String uniRequestTime) {
        this.uniRequestTime = uniRequestTime;
    }

    public UniRequestResultType getUniRequestResult() {
        return uniRequestResult;
    }

    public void setUniRequestResult(UniRequestResultType uniRequestResult) {
        this.uniRequestResult = uniRequestResult;
    }

    public String getUniRequestContext() {
        return uniRequestContext;
    }

    public void setUniRequestContext(String uniRequestContext) {
        this.uniRequestContext = uniRequestContext;
    }

    public int getUniRequesterId() {
        return uniRequesterId;
    }

    public void setUniRequesterId(int uniRequesterId) {
        this.uniRequesterId = uniRequesterId;
    }

    public int getUniRequestResponsibleId() {
        return uniRequestResponsibleId;
    }

    public void setUniRequestResponsibleId(int uniRequestResponsibleId) {
        this.uniRequestResponsibleId = uniRequestResponsibleId;
    }





}

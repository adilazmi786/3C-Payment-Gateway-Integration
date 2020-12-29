package com.payment.poc.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name="CreateTokenResult", namespace = "http://web2pay.com/5.0/2009/11/5.1.0/")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateTokenResult implements Serializable {

    @XmlElement(name="TokenNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenNo;
    
    @XmlElement(name="TokenMaskedNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenMaskedNo;
    
    @XmlElement(name="TokenExpiryYYMM", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenExpiryYYMM;
    
    @XmlElement(name="TokenProfileID", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenProfileID;
    
    @XmlElement(name="CardTypeCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardTypeCode;
    
    @XmlElement(name="CardNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardNo;
    
    @XmlElement(name="CardMaskedNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardMaskedNo;
    
    @XmlElement(name="CardBinNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardBinNo;
    
    @XmlElement(name="CardLast4No", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardLast4No;
    
    @XmlElement(name="CardExpiryYYMM", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardExpiryYYMM;
    
    @XmlElement(name="CardIssueYYMM", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardIssueYYMM;
    
    @XmlElement(name="CardMaskedNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardIssueNo;
    
    @XmlElement(name="CardHolderForename", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderForename;
    
    @XmlElement(name="CardMaskedNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderSurname;
    
    @XmlElement(name="CardHolderStreet", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderStreet;
    
    @XmlElement(name="CardHolderCity", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderCity;
    
    @XmlElement(name="CardHolderState", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderState;
    
    @XmlElement(name="CardHolderPostCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardHolderPostCode;
    
    @XmlElement(name="UserRef", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String userRef;
    
    @XmlElement(name="UserData1", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String userData1;
    
    @XmlElement(name="UserData2", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String userData2;
    
    @XmlElement(name="ExtraFunction", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String extraFunction;
    
    @XmlElement(name="Active", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String active;
    
    @XmlElement(name="ReturnText", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String returnText;
    
    @XmlElement(name="ReturnCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String returnCode;

    @Override
    public String toString() {
        return "CreateTokenResult [tokenNo=" + tokenNo + ", tokenMaskedNo=" + tokenMaskedNo + ", tokenExpiryYYMM="
                + tokenExpiryYYMM + ", tokenProfileID=" + tokenProfileID + ", cardTypeCode=" + cardTypeCode
                + ", cardNo=" + cardNo + ", cardMaskedNo=" + cardMaskedNo + ", cardBinNo=" + cardBinNo
                + ", cardLast4No=" + cardLast4No + ", cardExpiryYYMM=" + cardExpiryYYMM + ", cardIssueYYMM="
                + cardIssueYYMM + ", cardIssueNo=" + cardIssueNo + ", cardHolderForename=" + cardHolderForename
                + ", cardHolderSurname=" + cardHolderSurname + ", cardHolderStreet=" + cardHolderStreet
                + ", cardHolderCity=" + cardHolderCity + ", cardHolderState=" + cardHolderState
                + ", cardHolderPostCode=" + cardHolderPostCode + ", userRef=" + userRef + ", userData1=" + userData1
                + ", userData2=" + userData2 + ", extraFunction=" + extraFunction + ", active=" + active
                + ", returnText=" + returnText + ", returnCode=" + returnCode + "]";
    }
    
    
}

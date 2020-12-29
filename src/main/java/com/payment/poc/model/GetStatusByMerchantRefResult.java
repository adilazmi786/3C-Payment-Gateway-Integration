package com.payment.poc.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name="GetStatusByMerchantRefResult", namespace = "http://web2pay.com/5.0/2009/11/5.1.0/")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GetStatusByMerchantRefResult {

    @XmlElement(name="TxID", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String txID;
    
    @XmlElement(name="AuthorisationCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String authorisationCode;
    
    @XmlElement(name="RedirectURL", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String redirectURL;
    
    @XmlElement(name="AuthoriseAmount", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String authoriseAmount;
    
    @XmlElement(name="CaptureAmount", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String captureAmount;
    
    @XmlElement(name="Currency", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String currency;
    
    @XmlElement(name="MerchantRef", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String merchantRef;
    
    @XmlElement(name="MerchantCategory", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String merchantCategory;
    
    @XmlElement(name="ReturnText", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String returnText;
    
    @XmlElement(name="TxState", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String txState;
    
    @XmlElement(name="TxStateText", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String txStateText;
    
    @XmlElement(name="Cvv2ResultCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cvv2ResultCode;
    
    @XmlElement(name="AvsResultCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String avsResultCode;
    
    @XmlElement(name="ReturnCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String returnCode;
    
    @XmlElement(name="CardNumberLast4", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardNumberLast4;
    
    @XmlElement(name="CardType", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardType;
    
    @XmlElement(name="CardTypeName", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardTypeName;
    
    @XmlElement(name="CardExpiryYYMM", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String cardExpiryYYMM;
    
    @XmlElement(name="TokenNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenNo;
    
    @XmlElement(name="TokenExpiryYYMM", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
    private String tokenExpiryYYMM;

}

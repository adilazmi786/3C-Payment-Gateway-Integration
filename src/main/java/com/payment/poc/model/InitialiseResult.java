package com.payment.poc.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "InitialiseResult" ,namespace="http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class InitialiseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name ="ipgSession", namespace="http://tempuri.org/")
    private String ipgSession;
    
    @XmlElement(name ="ipgResultCode", namespace="http://tempuri.org/")
    private Integer ipgResultCode;
    
    @XmlElement(name ="ipgResultText", namespace="http://tempuri.org/")
    private String ipgResultText;
    
    @Override
    public String toString() {
        return "InitialiseResult [ipgSession=" + ipgSession + ", ipgResultCode=" + ipgResultCode + ", ipgResultText="
                + ipgResultText + "]";
    }
    
    

}

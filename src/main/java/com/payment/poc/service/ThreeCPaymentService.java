package com.payment.poc.service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.springframework.stereotype.Service;

import com.payment.poc.helper.ThreeCHelper;
import com.payment.poc.model.CreateTokenResult;
import com.payment.poc.model.InitialiseResult;
import com.payment.poc.model.PaymentResult;
import com.payment.poc.model.RefundResult;

@Service
public class ThreeCPaymentService {

    /**
     * @description get ipg session
     * @param merchantRef
     * @param amount
     * @return ipgsession
     */
    public InitialiseResult getIpgSession(String merchantRef, String amount) {
        try {
            String url = "https://web2payuat.3cint.com/ipage/Service/_2006_05_v1_0_1/initialiseservice.asmx?WSDL";
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <x:Header/> <x:Body> <tem:Initialise> <tem:security_emerchant_id>QikserveTest</tem:security_emerchant_id> <tem:security_validation_code>QikserveTest1</tem:security_validation_code> <tem:trx_merchant_reference>"
                    + merchantRef
                    + "</tem:trx_merchant_reference> <tem:trx_amount_currency_code>GBP</tem:trx_amount_currency_code> <tem:trx_amount_value>"
                    + amount
                    + "</tem:trx_amount_value> <tem:template_id></tem:template_id> <tem:posturl_success>http://localhost:8080/threecpay/success</tem:posturl_success> <tem:posturl_failure>http://localhost:8080/threecpay/failure</tem:posturl_failure> <tem:service_action>InitialiseServiceSoap</tem:service_action> <tem:trx_options>G</tem:trx_options> </tem:Initialise> </x:Body> </x:Envelope>";

            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://tempuri.org/Initialise";
            StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction , "POST");

            XMLStreamReader xmlStreamReader = ThreeCHelper.convertToXmlStream(response);
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.isStartElement()) {
                    String name = xmlStreamReader.getLocalName();
                    if (name.equalsIgnoreCase("InitialiseResult")) {
                        System.out.println(name);
                        break;
                    }
                }
                xmlStreamReader.next();
            }

            JAXBContext jxbContext = JAXBContext.newInstance(InitialiseResult.class);
            Unmarshaller unmarshaller = jxbContext.createUnmarshaller();
            JAXBElement<InitialiseResult> initializeResult = unmarshaller.unmarshal(xmlStreamReader,
                    InitialiseResult.class);
            return initializeResult.getValue();

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    /**
     * @description create token
     * @param merchantRef
     * @param amount
     * @return payment result
     * @throws Exception
     */
    public PaymentResult pay(String merchantRef, String amount) throws Exception {
        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Token.asmx?op=CreateToken";
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> <soap:Body> <CreateToken xmlns=\"http://web2pay.com/5.0/2009/11/5.1.0/\"> <eMerchantID>QikserveTest</eMerchantID> <ValidationCode>QikserveTest1</ValidationCode> <TokenSchemeID></TokenSchemeID> <TokenExpiryYYMM></TokenExpiryYYMM> <CardNumber>4111111111111103</CardNumber> <CardExpiryYYMM>2212</CardExpiryYYMM> <CardIssueYYMM></CardIssueYYMM> <CardIssueNo></CardIssueNo> <CardHolderAddress1>1281 West georgia</CardHolderAddress1> <CardHolderCity>Manchester</CardHolderCity> <CardHolderState>London</CardHolderState> <CardHolderPostalCode>SO140PN</CardHolderPostalCode> <CardHolderFirstName>Mohammad</CardHolderFirstName> <CardHolderLastName>Adil</CardHolderLastName> <MerchantRef>"
                    + merchantRef
                    + "</MerchantRef> <UserData1>nothing</UserData1> <UserData2>nothing</UserData2> <Online>True</Online> <OptionFlags>G</OptionFlags> </CreateToken> </soap:Body> </soap:Envelope>";
            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://web2pay.com/5.0/2009/11/5.1.0/CreateToken";
            StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction,"POST");

            XMLStreamReader xmlStreamReader = ThreeCHelper.convertToXmlStream(response);

            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.isStartElement()) {
                    String name = xmlStreamReader.getLocalName();
                    if (name.equalsIgnoreCase("CreateTokenResult")) {
                        break;
                    }
                }
                xmlStreamReader.next();
            }
            JAXBContext jxbContext = JAXBContext.newInstance(CreateTokenResult.class);
            Unmarshaller unmarshaller = jxbContext.createUnmarshaller();
            JAXBElement<CreateTokenResult> createTokenResult = unmarshaller.unmarshal(xmlStreamReader,
                    CreateTokenResult.class);
            if (createTokenResult.getValue().getReturnCode().equalsIgnoreCase("0")) {
                
                // Once token is generated , checking the authorization status
                PaymentResult payment = ThreeCHelper.checkAuthorization(createTokenResult.getValue().getUserRef(),
                        createTokenResult.getValue().getTokenNo(), amount);
                return payment;
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    /**
     * @description
     * @param txnId
     * @return Refund Result
     * @throws Exception
     */
    public RefundResult refund(String txnId) throws Exception {

        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Pay.asmx";
            String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.0.0/\"> <x:Header/> <x:Body> <ns:ReverseByTxID> <ns:eMerchantID>QikserveTest</ns:eMerchantID> <ns:ValidationCode>QikserveTest1</ns:ValidationCode> <ns:TxID>"
                    + txnId
                    + "</ns:TxID> <ns:OptionFlags>G</ns:OptionFlags> </ns:ReverseByTxID> </x:Body> </x:Envelope>";

            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://web2pay.com/5.0/2009/11/5.0.0/ReverseByTxID";
            StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction,"POST");

            XMLStreamReader xmlStreamReader = ThreeCHelper.convertToXmlStream(response);

            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.isStartElement()) {
                    String name = xmlStreamReader.getLocalName();
                    if (name.equalsIgnoreCase("ReverseByTxIDResult")) {
                        break;
                    }
                }
                xmlStreamReader.next();
            }

            JAXBContext jxbContext = JAXBContext.newInstance(RefundResult.class);
            Unmarshaller unmarshaller = jxbContext.createUnmarshaller();
            JAXBElement<RefundResult> refund = unmarshaller.unmarshal(xmlStreamReader, RefundResult.class);
            if (refund.getValue().getReturnCode().equalsIgnoreCase("0")) {
                return refund.getValue();
            } else {
                throw new Exception(refund.getValue().getReturnCode());
            }
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    public String ipageLoad(String ipgSession) {
        try {
            String url = "https://web2payuat.3cint.com/iPage/Service/_2006_05_v1_0_1/contact-six.aspx";
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <x:Header /> <x:Body> <tem:Initialise>  <tem:trx_merchant_reference>"
                    + "1067"
                    + "</tem:trx_merchant_reference> <tem:trx_amount_currency_code>GBP</tem:trx_amount_currency_code> <tem:trx_amount_value>"
                    + "1300"
                    + "</tem:trx_amount_value> <tem:template_id /> qikserve.xml <tem:posturl_success>http://localhost:8080/threecpay/success</tem:posturl_success> <tem:posturl_failure>http://localhost:8080/threecpay/failure</tem:posturl_failure> <tem:service_action>InitialiseServiceSoap</tem:service_action> <tem:trx_options>G</tem:trx_options> <tem:XXX_IPGSESSION_XXX>"+ipgSession+"</tem:XXX_IPGSESSION_XXX> </tem:Initialise> </x:Body> </x:Envelope>";

            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://web2pay.com/5.0/2009/11/5.0.0/InitialiseServiceSoap";
            StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");
            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}

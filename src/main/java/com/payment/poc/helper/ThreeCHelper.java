package com.payment.poc.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.payment.poc.config.ThreeCConfig;
import com.payment.poc.model.GetStatusByMerchantRefResult;
import com.payment.poc.model.PaymentResult;

/**
 * The Helper class
 *
 */
public class ThreeCHelper {

    /**
     * @description Get XML response from 3c payment
     * @param url
     * @param xml
     * @param contentType
     * @param soapAction
     * @param httpMethod
     * @return xml response
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     */
    public static StringBuffer getXMLResponse(String url, String xml, String contentType, String soapAction, String httpMethod)
            throws MalformedURLException, IOException, ProtocolException {
        HttpURLConnection con = createHttpConnection(url, contentType, soapAction, httpMethod);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(xml);
        wr.flush();
        wr.close();

        String responseStatus = con.getResponseMessage();
        System.out.println(responseStatus);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println("response:" + response.toString());
        return response;
    }
    
    /**
     * @description Convert StringBuffer into XMLStreamReader
     * @param response
     * @return xml stream reader
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    public static XMLStreamReader convertToXmlStream(StringBuffer response)
            throws FactoryConfigurationError, XMLStreamException {
        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        final StringReader inputReader = new StringReader(response.toString());
        XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(inputReader);
        return xmlStreamReader;
    }
    
    /**
     * @param url
     * @param contentType
     * @param soapAction
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     */
    public static HttpURLConnection createHttpConnection(String url, String contentType, String soapAction, String httpMethod)
            throws MalformedURLException, IOException, ProtocolException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(httpMethod);
        con.setRequestProperty("Content-Type", contentType);
        con.setRequestProperty("SOAPAction", soapAction);
        con.setDoOutput(true);
        return con;
    }
    
    /**
     * @description checking the authorization status
     * @param merchantRef
     * @param token
     * @param amount
     * @return payment result
     * @throws Exception
     */
    public static PaymentResult checkAuthorization(String merchantRef, String token, String amount, ThreeCConfig config) throws Exception {
        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Authorise.asmx";
            String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.1.0/\"> <x:Header/> <x:Body> <ns:GetStatusByMerchantRef> <ns:eMerchantID>"+config.getEMerchantId()+"</ns:eMerchantID> <ns:ValidationCode>"+config.getValidationCode()+"</ns:ValidationCode> <ns:MerchantRef>"
                    + merchantRef
                    + "</ns:MerchantRef> <ns:OptionFlags>G</ns:OptionFlags> </ns:GetStatusByMerchantRef> </x:Body> </x:Envelope>";
            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://web2pay.com/5.0/2009/11/5.1.0/GetStatusByMerchantRef";
            StringBuffer response = getXMLResponse(url, xml, contentType, soapAction, "POST");

            XMLStreamReader xmlStreamReader = convertToXmlStream(response);

            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.isStartElement()) {
                    String name = xmlStreamReader.getLocalName();
                    if (name.equalsIgnoreCase("GetStatusByMerchantRefResult")) {
                        break;
                    }
                }
                xmlStreamReader.next();
            }

            JAXBContext jxbContext = JAXBContext.newInstance(GetStatusByMerchantRefResult.class);
            Unmarshaller unmarshaller = jxbContext.createUnmarshaller();
            JAXBElement<GetStatusByMerchantRefResult> getAuthStatus = unmarshaller.unmarshal(xmlStreamReader,
                    GetStatusByMerchantRefResult.class);
            
            // authorization is success , creating the payment transaction
            if (getAuthStatus.getValue().getReturnText().equalsIgnoreCase("APPROVED")) {
                return createPayment(merchantRef, token, amount, config);
            } else {
                throw new Exception(getAuthStatus.getValue().getReturnText());
            }
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
    
    /**
     * Creating payment transaction
     * @param merchantRef
     * @param token
     * @param amount
     * @return payment result
     * @throws Exception
     */
    private static PaymentResult createPayment(String merchantRef, String token, String amount, ThreeCConfig config) throws Exception {
        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Pay.asmx";
            String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.0.0/\"> <x:Header/> <x:Body> <ns:RequestNoCardRead> <ns:eMerchantID>"+config.getEMerchantId()+"</ns:eMerchantID> <ns:ValidationCode>"+config.getValidationCode()+"</ns:ValidationCode> <ns:PaymentOkUrl></ns:PaymentOkUrl> <ns:CardNumber>"
                    + token
                    + "</ns:CardNumber> <ns:CardExpiryYYMM>2212</ns:CardExpiryYYMM> <ns:CardIssueYYMM></ns:CardIssueYYMM> <ns:CardIssueNo></ns:CardIssueNo> <ns:CardCvv2></ns:CardCvv2> <ns:CardHolderAddress1>1281 West Georgia</ns:CardHolderAddress1> <ns:CardHolderCity>Manchester</ns:CardHolderCity> <ns:CardHolderState>London</ns:CardHolderState> <ns:CardHolderPostalCode>SO140PN</ns:CardHolderPostalCode> <ns:CardHolderFirstName>Mohammad</ns:CardHolderFirstName> <ns:CardHolderLastName>Adil</ns:CardHolderLastName> <ns:Amount>"
                    + amount + "</ns:Amount> <ns:Currency>GBP</ns:Currency> <ns:MerchantRef>" + merchantRef
                    + "</ns:MerchantRef> <ns:UserData1>nothing</ns:UserData1> <ns:UserData2>nothing</ns:UserData2> <ns:UserData3>nothing</ns:UserData3> <ns:UserData4>nothing</ns:UserData4> <ns:UserData5>nothing</ns:UserData5> <ns:OptionFlags>P</ns:OptionFlags> </ns:RequestNoCardRead> </x:Body> </x:Envelope>";

            String contentType = "text/xml; charset=utf-8";
            String soapAction = "http://web2pay.com/5.0/2009/11/5.0.0/RequestNoCardRead";
            StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");

            XMLStreamReader xmlStreamReader = convertToXmlStream(response);

            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.isStartElement()) {
                    String name = xmlStreamReader.getLocalName();
                    if (name.equalsIgnoreCase("RequestNoCardReadResult")) {
                        break;
                    }
                }
                xmlStreamReader.next();
            }

            JAXBContext jxbContext = JAXBContext.newInstance(PaymentResult.class);
            Unmarshaller unmarshaller = jxbContext.createUnmarshaller();
            JAXBElement<PaymentResult> payment = unmarshaller.unmarshal(xmlStreamReader, PaymentResult.class);
            if (payment.getValue().getReturnCode().equalsIgnoreCase("0")) {
                return payment.getValue();
            } else {
                throw new Exception(payment.getValue().getReturnCode());
            }
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
}

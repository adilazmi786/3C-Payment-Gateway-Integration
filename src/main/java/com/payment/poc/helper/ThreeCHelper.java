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
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.payment.poc.config.ThreeCConfig;
import com.payment.poc.constants.ThreeCEnum;
import com.payment.poc.exception.APIException;
import com.payment.poc.model.CreateTokenResult;
import com.payment.poc.model.GetStatusByMerchantRefResult;
import com.payment.poc.model.InitialiseResult;
import com.payment.poc.model.PaymentResult;
import com.payment.poc.model.RefundResult;

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
    public static StringBuffer getXMLResponse(String url, String xml, String contentType, String soapAction,
            String httpMethod) throws MalformedURLException, IOException, ProtocolException {
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
    public static HttpURLConnection createHttpConnection(String url, String contentType, String soapAction,
            String httpMethod) throws MalformedURLException, IOException, ProtocolException {
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
    public static PaymentResult checkAuthorization(String merchantRef, String token, String amount, ThreeCConfig config)
            throws APIException, Exception {
        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Authorise.asmx";
            String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.1.0/\"> <x:Header/> <x:Body> <ns:GetStatusByMerchantRef> <ns:eMerchantID>"
                    + config.getEMerchantId() + "</ns:eMerchantID> <ns:ValidationCode>" + config.getValidationCode()
                    + "</ns:ValidationCode> <ns:MerchantRef>" + merchantRef
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
            if (getAuthStatus.getValue().getReturnText().equalsIgnoreCase(ThreeCEnum.APPROVED.toString())) {
                return createPayment(merchantRef, token, amount, config);
            } else {
                throw new APIException(getAuthStatus.getValue().getReturnText());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Creating payment transaction
     * 
     * @param merchantRef
     * @param token
     * @param amount
     * @return payment result
     * @throws Exception
     */
    private static PaymentResult createPayment(String merchantRef, String token, String amount, ThreeCConfig config)
            throws APIException, Exception {
        try {
            String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Pay.asmx";
            String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.0.0/\"> <x:Header/> <x:Body> <ns:RequestNoCardRead> <ns:eMerchantID>"
                    + config.getEMerchantId() + "</ns:eMerchantID> <ns:ValidationCode>" + config.getValidationCode()
                    + "</ns:ValidationCode> <ns:PaymentOkUrl></ns:PaymentOkUrl> <ns:CardNumber>" + token
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
                throw new APIException(payment.getValue().getReturnText() +" with error code "+payment.getValue().getReturnCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static PaymentResult getTxnDetails(String txnId, String amount, ThreeCConfig config)
            throws APIException, Exception {

        String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Authorise.asmx";
        String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.1.0/\"> <x:Header/> <x:Body> <ns:GetStatusByTxID> <ns:eMerchantID>"
                + config.getEMerchantId() + "</ns:eMerchantID> <ns:ValidationCode>" + config.getValidationCode()
                + "</ns:ValidationCode> <ns:TxID>" + txnId
                + "</ns:TxID> <ns:OptionFlags>G</ns:OptionFlags> </ns:GetStatusByTxID> </x:Body> </x:Envelope>";
        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://web2pay.com/5.0/2009/11/5.1.0/GetStatusByTxID";
        StringBuffer response = getXMLResponse(url, xml, contentType, soapAction, "POST");

        XMLStreamReader xmlStreamReader = convertToXmlStream(response);

        while (xmlStreamReader.hasNext()) {
            if (xmlStreamReader.isStartElement()) {
                String name = xmlStreamReader.getLocalName();
                if (name.equalsIgnoreCase("GetStatusByTxIDResult")) {
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
        if (getAuthStatus.getValue().getReturnText().equalsIgnoreCase(ThreeCEnum.APPROVED.toString())) {

            return createPayment(getAuthStatus.getValue().getMerchantRef(), getAuthStatus.getValue().getTokenNo(),
                    amount, config);
        } else {
            throw new APIException(getAuthStatus.getValue().getReturnText() +" with error code "+ getAuthStatus.getValue().getReturnCode());
        }

    }

    public static InitialiseResult _GetInitializedIpSession(String merchantRef, String amount, ThreeCConfig config)
            throws APIException, Exception {
        String url = "https://web2payuat.3cint.com/ipage/Service/_2006_05_v1_0_1/initialiseservice.asmx?WSDL";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <x:Header/> <x:Body> <tem:Initialise> <tem:security_emerchant_id>"
                + config.getEMerchantId() + "</tem:security_emerchant_id> <tem:security_validation_code>"
                + config.getValidationCode() + "</tem:security_validation_code> <tem:trx_merchant_reference>"
                + merchantRef
                + "</tem:trx_merchant_reference> <tem:trx_amount_currency_code>GBP</tem:trx_amount_currency_code> <tem:trx_amount_value>"
                + amount + "</tem:trx_amount_value><tem:template_id>" + config.getTemplateId()
                + "</tem:template_id> <tem:posturl_success>http://localhost:8080/threecpay/success</tem:posturl_success> <tem:posturl_failure>http://localhost:8080/threecpay/failure</tem:posturl_failure> <tem:service_action>InitialiseServiceSoap</tem:service_action> <tem:trx_options>G</tem:trx_options> </tem:Initialise> </x:Body> </x:Envelope>";

        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://tempuri.org/Initialise";
        StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "GET");

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
    }

    public static RefundResult _GetReverseCapture(String txnId, ThreeCConfig config) throws Exception {
        String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Pay.asmx";
        String xml = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://web2pay.com/5.0/2009/11/5.0.0/\"> <x:Header/> <x:Body> <ns:ReverseByTxID> <ns:eMerchantID>"
                + config.getEMerchantId() + "</ns:eMerchantID> <ns:ValidationCode>" + config.getValidationCode()
                + "</ns:ValidationCode> <ns:TxID>" + txnId
                + "</ns:TxID> <ns:OptionFlags>G</ns:OptionFlags> </ns:ReverseByTxID> </x:Body> </x:Envelope>";

        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://web2pay.com/5.0/2009/11/5.0.0/ReverseByTxID";
        StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");

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
        return refund.getValue();
    }

    public static InitialiseResult _GetIpgSessionWithToken(String merchantRef, String amount, ThreeCConfig config)
            throws Exception {

        UUID uuid = UUID.randomUUID();

        String url = "https://web2payuat.3cint.com/ipage/Service/_2006_05_v1_0_1/initialiseservice.asmx?WSDL";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <x:Header/> <x:Body> <tem:InitialiseWithToken> <tem:security_emerchant_id>"
                + config.getEMerchantId() + "</tem:security_emerchant_id> <tem:security_validation_code>"
                + config.getValidationCode() + "</tem:security_validation_code> <tem:trx_merchant_reference>"
                + merchantRef
                + "</tem:trx_merchant_reference> <tem:trx_amount_currency_code>GBP</tem:trx_amount_currency_code> <tem:trx_amount_value>"
                + amount + "</tem:trx_amount_value><tem:template_id>" + config.getTemplateId()
                + "</tem:template_id> <tem:posturl_success>http://localhost:4200/threecpay/authorise</tem:posturl_success> <tem:posturl_failure>http://localhost:4200/threecpay/authorise</tem:posturl_failure> <tem:service_action>InitialiseWithToken</tem:service_action> <tem:token_no>?</tem:token_no> "
                + uuid
                + "<tem:token_injection_action></tem:token_injection_action> <tem:trx_options>G</tem:trx_options> </tem:InitialiseWithToken> </x:Body> </x:Envelope>";

        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://tempuri.org/InitialiseWithToken";
        StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");

        XMLStreamReader xmlStreamReader = ThreeCHelper.convertToXmlStream(response);
        while (xmlStreamReader.hasNext()) {
            if (xmlStreamReader.isStartElement()) {
                String name = xmlStreamReader.getLocalName();
                if (name.equalsIgnoreCase("InitialiseWithTokenResult")) {
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
    }

    public static String _GetIpage(String ipgSession, ThreeCConfig config) throws Exception {
        String url = "https://web2payuat.3cint.com/iPage/Service/_2006_05_v1_0_1/service.aspx";
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <x:Header /> <x:Body> <tem:Initialise>  <tem:trx_merchant_reference>"
                + "1067"
                + "</tem:trx_merchant_reference> <tem:trx_amount_currency_code>GBP</tem:trx_amount_currency_code> <tem:trx_amount_value>"
                + "1300"
                + "</tem:trx_amount_value> <tem:template_id /> qikserve.xml <tem:posturl_success>http://localhost:8080/threecpay/success</tem:posturl_success> <tem:posturl_failure>http://localhost:8080/threecpay/failure</tem:posturl_failure> <tem:service_action>authorise</tem:service_action> <tem:trx_options>G</tem:trx_options> <tem:XXX_IPGSESSION_XXX>"
                + ipgSession + "</tem:XXX_IPGSESSION_XXX> </tem:Initialise> </x:Body> </x:Envelope>";

        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://web2pay.com/5.0/2009/11/5.0.0/authorise";
        StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");
        return response.toString();
    }

    public static CreateTokenResult _GetToken(String merchantRef, ThreeCConfig config) throws Exception {
        String url = "https://web2payuat.3cint.com/mxg/service/_2011_02_v5_1_0/Token.asmx?op=CreateToken";
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> <soap:Body> <CreateToken xmlns=\"http://web2pay.com/5.0/2009/11/5.1.0/\"> <eMerchantID>"
                + config.getEMerchantId() + "</eMerchantID> <ValidationCode>" + config.getValidationCode()
                + "</ValidationCode> <TokenSchemeID></TokenSchemeID> <TokenExpiryYYMM></TokenExpiryYYMM> <CardNumber>4111111111111103</CardNumber> <CardExpiryYYMM>2212</CardExpiryYYMM> <CardIssueYYMM></CardIssueYYMM> <CardIssueNo></CardIssueNo> <CardHolderAddress1>1281 West georgia</CardHolderAddress1> <CardHolderCity>Manchester</CardHolderCity> <CardHolderState>London</CardHolderState> <CardHolderPostalCode>SO140PN</CardHolderPostalCode> <CardHolderFirstName>Mohammad</CardHolderFirstName> <CardHolderLastName>Adil</CardHolderLastName> <MerchantRef>"
                + merchantRef
                + "</MerchantRef> <UserData1>nothing</UserData1> <UserData2>nothing</UserData2> <Online>True</Online> <OptionFlags>G</OptionFlags> </CreateToken> </soap:Body> </soap:Envelope>";
        String contentType = "text/xml; charset=utf-8";
        String soapAction = "http://web2pay.com/5.0/2009/11/5.1.0/CreateToken";
        StringBuffer response = ThreeCHelper.getXMLResponse(url, xml, contentType, soapAction, "POST");

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
        return createTokenResult.getValue();
    }
}

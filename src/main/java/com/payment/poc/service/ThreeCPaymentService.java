package com.payment.poc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.poc.config.ThreeCConfig;
import com.payment.poc.exception.APIException;
import com.payment.poc.helper.ThreeCHelper;
import com.payment.poc.model.CreateTokenResult;
import com.payment.poc.model.InitialiseResult;
import com.payment.poc.model.PaymentResult;
import com.payment.poc.model.RefundResult;

/**
 * The Service class
 *
 */
@Service
public class ThreeCPaymentService {

    @Autowired
    ThreeCConfig config;

    /**
     * @description Find the Ipgession
     * @param merchantRef
     * @param amount
     * @return Ipgsession
     */
    public InitialiseResult getIpgSession(String merchantRef, String amount) throws APIException, Exception {

        try {
            InitialiseResult initializeResult = ThreeCHelper._GetInitializedIpSession(merchantRef, amount, config);
            if (initializeResult.getIpgResultText().equalsIgnoreCase("Success"))
                return initializeResult;
            else {
                throw new APIException(initializeResult.getIpgResultText() + " with Error code "
                        + initializeResult.getIpgResultCode());
            }
        } catch (Exception e) {
            throw e;
        }

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
            CreateTokenResult createTokenResult = ThreeCHelper._GetToken(merchantRef, config);
            if (createTokenResult.getReturnCode().equalsIgnoreCase("0")) {

                // Once token is generated , checking the authorization status
                PaymentResult payment = ThreeCHelper.checkAuthorization(createTokenResult.getUserRef(),
                        createTokenResult.getTokenNo(), amount, config);
                return payment;
            } else {
                throw new APIException(
                        createTokenResult.getReturnText() + " with error code: " + createTokenResult.getReturnCode());
            }

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @description
     * @param txnId
     * @return Refund Result
     * @throws Exception
     */
    public RefundResult reverseCapture(String txnId) throws APIException, Exception {

        try {
            RefundResult refund = ThreeCHelper._GetReverseCapture(txnId, config);
            if (refund.getReturnCode().equalsIgnoreCase("0")) {
                return refund;
            } else {
                throw new APIException(refund.getReturnText() + " with error code " + refund.getReturnCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @description
     * @param ipgSession
     * @return Ipage
     * @throws Exception
     */
    public String ipageLoad(String ipgSession) throws Exception {
        try {
            String response = ThreeCHelper._GetIpage(ipgSession, config);
            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param txnId
     * @param amount
     * @return payment result
     * @throws APIException
     * @throws Exception
     */
    public PaymentResult payWithTxnId(String txnId, String amount) throws APIException, Exception {

        PaymentResult payment = ThreeCHelper.getTxnDetails(txnId, amount, config);
        return payment;
    }

    /**
     * Ipg session with token
     * 
     * @param merchantRef
     * @param amount
     * @return Ipg session
     * @throws Exception
     */
    public InitialiseResult getIpgSessionWithToken(String merchantRef, String amount) throws Exception {

        try {
            InitialiseResult initializeResult = ThreeCHelper._GetIpgSessionWithToken(merchantRef, amount, config);
            if (initializeResult.getIpgResultText().equalsIgnoreCase("Success"))
                return initializeResult;
            else
                throw new APIException(
                        initializeResult.getIpgResultText() + " with error code" + initializeResult.getIpgResultCode());

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * create token
     * 
     * @param merchantRef
     * @param amount
     * @return token
     * @throws APIException
     * @throws Exception
     */
    public CreateTokenResult createToken(String merchantRef) throws APIException, Exception {
        try {
            CreateTokenResult createTokenResult = ThreeCHelper._GetToken(merchantRef, config);
            if (createTokenResult.getReturnCode().equalsIgnoreCase("0")) {
                return createTokenResult;
            } else {
                throw new APIException(
                        createTokenResult.getReturnText() + " with error code: " + createTokenResult.getReturnCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

}

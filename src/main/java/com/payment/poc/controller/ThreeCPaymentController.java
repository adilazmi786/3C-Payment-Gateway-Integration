package com.payment.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.payment.poc.exception.APIException;
import com.payment.poc.model.InitialiseResult;
import com.payment.poc.model.PaymentResult;
import com.payment.poc.model.RefundResult;
import com.payment.poc.service.ThreeCPaymentService;

@RestController
@CrossOrigin
@RequestMapping("threecpay")
public class ThreeCPaymentController {

    @Autowired
    ThreeCPaymentService threeCService;

    @GetMapping(value = "ipage", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> ipageLoad(@RequestParam(value = "ipgSession") String ipgSession) throws Exception {

        String response = threeCService.ipageLoad(ipgSession);

        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @GetMapping(value = "initialise/{merchantRef}/{amount}")
    public ResponseEntity<InitialiseResult> intialise(@PathVariable String merchantRef, @PathVariable String amount)
            throws APIException,Exception {

        InitialiseResult response = threeCService.getIpgSession(merchantRef, amount);

        return new ResponseEntity<InitialiseResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "initialiseWithToken/{merchantRef}/{amount}")
    public ResponseEntity<InitialiseResult> intialiseWithToken(@PathVariable String merchantRef,
            @PathVariable String amount) throws APIException {

        InitialiseResult response = threeCService.getIpgSessionWithToken(merchantRef, amount);

        return new ResponseEntity<InitialiseResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "pay/{merchantRef}/{amount}")
    public ResponseEntity<PaymentResult> createPayment(@PathVariable String merchantRef, @PathVariable String amount)
            throws Exception {

        PaymentResult response = threeCService.pay(merchantRef, amount);

        return new ResponseEntity<PaymentResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "payWithTxnId/{txnId}/{amount}")
    public ResponseEntity<PaymentResult> payTxnId(@PathVariable String txnId, @PathVariable String amount)
            throws APIException, Exception {
        PaymentResult response = threeCService.payWithTxnId(txnId, amount);
        return new ResponseEntity<PaymentResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "reverse-capture/{txnId}")
    public ResponseEntity<RefundResult> reverseCapture(@PathVariable String txnId) throws APIException, Exception {

        RefundResult response = threeCService.reverseCapture(txnId);
        return new ResponseEntity<RefundResult>(response, HttpStatus.OK);
    }

}

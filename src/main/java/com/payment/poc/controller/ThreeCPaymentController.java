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
    public ResponseEntity<String> ipageLoad(@RequestParam(value="ipgSession") String ipgSession) {

        String response = null;
        try {
            response = threeCService.ipageLoad(ipgSession);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @GetMapping(value = "initialise/{merchantRef}/{amount}")
    public ResponseEntity<InitialiseResult> intialise(@PathVariable String merchantRef, @PathVariable String amount) {

        InitialiseResult response = null;
        try {
            response = threeCService.getIpgSession(merchantRef, amount);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<InitialiseResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "initialiseWithToken/{merchantRef}/{amount}")
    public ResponseEntity<InitialiseResult> intialiseWithToken(@PathVariable String merchantRef, @PathVariable String amount) {

        InitialiseResult response = null;
        try {
            response = threeCService.getIpgSessionWithToken(merchantRef, amount);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<InitialiseResult>(response, HttpStatus.OK);
    }
    
    @GetMapping(value = "pay/{merchantRef}/{amount}")
    public ResponseEntity<PaymentResult> createPayment(@PathVariable String merchantRef, @PathVariable String amount) throws Exception {
        PaymentResult response = null;
        try {
            response = threeCService.pay(merchantRef, amount);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<PaymentResult>(response, HttpStatus.OK);
    }
    
    @GetMapping(value = "payWithTxnId/{txnId}/{amount}")
    public ResponseEntity<PaymentResult> payTxnId(@PathVariable String txnId, @PathVariable String amount) throws Exception {
        PaymentResult response = null;
        try {
            response = threeCService.payWithTxnId(txnId, amount);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<PaymentResult>(response, HttpStatus.OK);
    }

    @GetMapping(value = "reverse-capture/{txnId}")
    public ResponseEntity<RefundResult> reverseCapture(@PathVariable String txnId) throws Exception {
        RefundResult response = null;
        try {
            response = threeCService.reverseCapture(txnId);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<RefundResult>(response, HttpStatus.OK);
    }

}

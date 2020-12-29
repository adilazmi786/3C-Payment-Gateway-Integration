package com.payment.poc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<String> ipageLoad() {

        String response = null;
        try {
            response = threeCService.ipageLoad();
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

    @PostMapping(value = "success")
    public void success(HttpServletRequest req, HttpServletResponse res) {

        System.out.println("cc_response=>" + req.getParameterNames());
        res.setHeader("Location", "localhost:4200/threecpay");
        res.setStatus(302);
        // do capture
    }

    @PostMapping(value = "failure")
    public void failure(HttpServletRequest req, HttpServletResponse res) {

        System.out.println("cc_response=>" + req.getParameterNames());
        res.setHeader("Location", "localhost:4200/threecpay");
        res.setStatus(302);
        // do something
    }

    @GetMapping(value = "refund/{txnId}")
    public ResponseEntity<RefundResult> refund(@PathVariable String txnId) throws Exception {
        RefundResult response = null;
        try {
            response = threeCService.refund(txnId);
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.", e);
        }
        return new ResponseEntity<RefundResult>(response, HttpStatus.OK);
    }

}

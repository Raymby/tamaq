package com.example.tamaq.Controllers;

import org.springframework.ui.Model;
import com.example.tamaq.Services.OrderService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/paypal")
public class PayPalController {

    @Autowired
    private APIContext apiContext;

    @Autowired
    private OrderService orderService;

    @PostMapping("/pay")
    public String pay(@RequestParam("total") double total, @RequestParam("currency") String currency, Model model) {
        try {
            // Создание платежа
            Payment payment = createPayment(total, currency, "http://localhost:8080/paypal/success", "http://localhost:8080/paypal/cancel");
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();  // Перенаправление на PayPal для подтверждения
                }
            }
        } catch (PayPalRESTException e) {
            model.addAttribute("errorMessage", "Ошибка при обработке платежа через PayPal: " + e.getMessage());
            return "checkout";  // Вернуться на страницу оформления заказа в случае ошибки
        }
        return "checkout";  // Вернуться на страницу оформления заказа, если что-то пошло не так
    }

    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, Model model) {
        try {
            // Завершение платежа
            Payment payment = executePayment(paymentId, payerId);
            if ("approved".equals(payment.getState())) {
                model.addAttribute("successMessage", "Платеж через PayPal успешно завершен!");
                return "order-confirmation";  // Перейти на страницу подтверждения заказа
            }
        } catch (PayPalRESTException e) {
            model.addAttribute("errorMessage", "Ошибка при подтверждении платежа: " + e.getMessage());
            return "checkout";  // Вернуться на страницу оформления заказа в случае ошибки
        }
        return "checkout";  // Вернуться на страницу оформления заказа, если что-то пошло не так
    }

    @GetMapping("/cancel")
    public String cancelPay(Model model) {
        model.addAttribute("errorMessage", "Платеж через PayPal был отменен.");
        return "checkout";  // Вернуться на страницу оформления заказа
    }

    private Payment createPayment(double total, String currency, String successUrl, String cancelUrl) throws PayPalRESTException {
        // Создание суммы
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        // Создание транзакции
        Transaction transaction = new Transaction();
        transaction.setDescription("Оплата заказа через PayPal");
        transaction.setAmount(amount);

        // Создание списка транзакций
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // Указание метода платежа
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Создание платежа
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // Указание URL для успешного завершения и отмены
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        // Создание платежа через PayPal API
        return payment.create(apiContext);  // Correctly using the API context
    }

    private Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        // Выполнение платежа
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecution);  // Correctly using the API context
    }
}

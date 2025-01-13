package com.example.tamaq.Services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(double total, String currency, String successUrl, String cancelUrl) throws PayPalRESTException {
        // Проверка на положительность и правильный формат суммы
        if (total <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        // Форматируем сумму
        String formattedTotal = String.format("%.2f", total).replace(",", "."); // Заменяем запятые на точки
        System.out.println("Создание платежа: сумма = " + formattedTotal + ", валюта = " + currency);

        // Создание суммы
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(formattedTotal); // Используем форматированную сумму

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
        return payment.create(apiContext);
    }
}

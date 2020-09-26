package edu.uci.ics.AESMERAL.service.billing.core;

import com.braintreepayments.http.serializer.Json;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.paypal.orders.*;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PayPalOrderClient {
    private static final String clientID = "AW1zeeE8x-dZLh4_hPFRtVXd--qxHpXGNIGjaCEPLmDir18exT-HaGtKaI99m_ckT0D-Xh0HQh628tdR";
    private static final String clientSecret = "EANR60Uxy6yBerxNVWPV8qeHMaDuXc1Gqku_IQ3vl7_bQLXLewjCE22jiNcTwl3BgYSt7z4xuXB_o249";
    public PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientID,clientSecret);
    public PayPalHttpClient client = new PayPalHttpClient(environment);

    public static OrderInformation createPayPalOrder(PayPalOrderClient orderClient, Float price)          // items should have value and quantity.
    {
        Order order = null;

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext().returnUrl("http://0.0.0.0:3000/complete");

        orderRequest.applicationContext(applicationContext);
        List<PurchaseUnitRequest> purchasedUnits = new ArrayList<>();
        // todo Purchase add.. (just pass price?)
        String priceInString = String.format("%.2f", price);
        purchasedUnits.add(new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(priceInString)));
        orderRequest.purchaseUnits(purchasedUnits);

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        try{
            HttpResponse<Order> response = orderClient.client.execute(request);
            order = response.result();
            ServiceLogger.LOGGER.info("Order id: " + order.id());
            ServiceLogger.LOGGER.info(order.links().get(1).rel() + " -> " + order.links().get(1).href());
            order.links().forEach(link -> ServiceLogger.LOGGER.info(link.rel() + " -> " + link.method() + ":" + link.href()));
            return new OrderInformation(order.links().get(1).href(), order.id());

        } catch (IOException ioe) {
            if(ioe instanceof HttpException) {
                HttpException he = (HttpException) ioe;
                ServiceLogger.LOGGER.info(he.getMessage());
                he.headers().forEach(x -> ServiceLogger.LOGGER.info("HttpError : " + x + " :" + he.headers().header(x)));
            } else {
                ServiceLogger.LOGGER.info("Something went wrong here");
            }
            return null;
        }
    }
    public static String CaptureOrder(String orderID, PayPalOrderClient orderClient)
    {
        Order order;
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderID);

        try{
            HttpResponse<Order> response = orderClient.client.execute(request);
            order = response.result();
            ServiceLogger.LOGGER.info("Capture ID: " + order.purchaseUnits().get(0).payments().captures().get(0).id());
            order.purchaseUnits().get(0).payments().captures().get(0).links().forEach(link -> ServiceLogger.LOGGER.info(link.rel() + " -> " + link.method()
            + ":" + link.href()));
            return order.purchaseUnits().get(0).payments().captures().get(0).id();
        } catch (IOException ioe){
            if(ioe instanceof HttpException) {
                HttpException he = (HttpException) ioe;
                ServiceLogger.LOGGER.info(he.getMessage());
                he.headers().forEach(x -> ServiceLogger.LOGGER.info(x + " :" + he.headers().header(x)));
            } else
            {
                ServiceLogger.LOGGER.info("something went wrong here");
            }
            return null;
        }
    }
    public static String getOrder(String orderID, PayPalOrderClient orderClient) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(orderID);
        HttpResponse<Order> response = orderClient.client.execute(request);
        //ServiceLogger.LOGGER.info("Full body response: " + (new Json().serialize(response.result())));
        return new Json().serialize(response.result());
    }
}

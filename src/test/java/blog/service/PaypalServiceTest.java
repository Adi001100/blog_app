package blog.service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

class PaypalServiceTest {

    private APIContext apiContext;

    private PaypalService underTest;

    @Before
    void init() {
        apiContext = mock(APIContext.class);
        underTest = new PaypalService(apiContext);
    }

    @Test
    void createPayment() throws PayPalRESTException {
        apiContext = mock(APIContext.class);
        underTest = new PaypalService(apiContext);
        Payment payment = mock(Payment.class);

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format(Locale.forLanguageTag("USD"), "%.2f", 12.0)); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription("desc");
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        underTest.createPayment(payment, 12.0, "USD", "paypal", "intent", "desc", "URL", "URL");

        verify(payment, times(1)).create(apiContext);
        verify(payment, times(1)).setTransactions(transactions);
    }

    @Test
    void executePayment() {
    }
}
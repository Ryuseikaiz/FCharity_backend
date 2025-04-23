package fptu.fcharity.helpers.payment;


import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import fptu.fcharity.dto.payment.*;
import fptu.fcharity.response.payment.CreateAccountLinkResponse;
import fptu.fcharity.response.payment.CreateAccountResponse;
import fptu.fcharity.response.payment.CreateCheckoutResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class StripeService {


    public Price createPrice(PriceDto dto) throws StripeException {
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency(dto.getCurrency())
                        .setUnitAmount(dto.getAmount())
                        .setProductData(
                                PriceCreateParams.ProductData.builder().setName(dto.getProductName()).build()
                        )
                        .build();
        Price price = Price.create(params);
        return price;
    }
    public PaymentLink createPaymentLink(CreatePaymentLinkDto dto ) throws StripeException {
        PaymentLinkCreateParams params =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice(dto.getPriceId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setOnBehalfOf(dto.getSrcAccountId())
                        .setTransferData(
                                PaymentLinkCreateParams.TransferData.builder()
                                        .setDestination(dto.getDesAccountId())
                                        .build()
                        )
                        .build();

        PaymentLink paymentLink = PaymentLink.create(params);


        return paymentLink;
    }
    public CreateCheckoutResponse checkoutProducts(CreateCheckoutDto productRequest) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl("https://example.com/success")
                        .setCancelUrl("https://example.com/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(productRequest.getPriceId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .build();

        Session session = Session.create(params);

        return CreateCheckoutResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    public CreateAccountLinkResponse createAccountLink(CreateAccountLinkDto createAccountLinkDto) throws StripeException {
        String connectedAccountId = createAccountLinkDto.getAccount();
            AccountLink accountLink = AccountLink.create(
                    AccountLinkCreateParams.builder()
                            .setAccount(connectedAccountId)
                            .setReturnUrl("http://localhost:4242/return/" + connectedAccountId)
                            .setRefreshUrl("http://localhost:4242/refresh/" + connectedAccountId)
                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                            .build()
            );
            CreateAccountLinkResponse accountLinkResponse = new CreateAccountLinkResponse(accountLink.getUrl());
            return accountLinkResponse;

    }

    public CreateAccountResponse createAccount(CreateAccountDto dto) throws StripeException {
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS) // Set account type
                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL) // Example: Individual business type
                .setCountry(dto.getCountryCode().toUpperCase(Locale.ROOT)) // Set the country of the account
                .setEmail(dto.getEmail()) // The email of the connected account
                .build();

        Account account = Account.create(params);

            CreateAccountResponse accountResponse = new CreateAccountResponse(account.getId());
            return accountResponse;
    }

    public Transfer createTransfer(CreateTransferDto dto) throws StripeException {
        TransferCreateParams params =
                TransferCreateParams.builder()
                        .setAmount(Long.parseLong(dto.getAmount().toString()))
                        .setCurrency(dto.getCurrency())
                        .setDestination(dto.getAccount())
                        .setTransferGroup(dto.getTransferGroup())
                        .build();
        Transfer transfer = Transfer.create(params);
        return transfer;
    }

    public TransferCollection listTransfers(String accountId) throws StripeException {
        TransferListParams params = TransferListParams.builder().setLimit(3L).build();
        TransferCollection transfers = Transfer.list(params);
        return transfers;
    }

    public Charge createCharge(CreateChargeDto dto) throws StripeException {
        ChargeCreateParams params =
                ChargeCreateParams.builder()
                        .setAmount(dto.getAmount())
                        .setCurrency(dto.getCurrency())
                        .setSource(dto.getToken())
                        .build();
        Charge charge = Charge.create(params);
        return charge;
    }
    public Payout createPayout(CreatePayoutDto dto) throws StripeException {
        PayoutCreateParams params = PayoutCreateParams.builder()
                .setAmount(dto.getAmount()) // Số tiền theo cent
                .setCurrency(dto.getCurrency())
                .setDescription(dto.getDescription())
                .setDestination(dto.getAccountId()) // ID của tài khoản Connect
                .build();

        Payout payout = Payout.create(params);
        return payout;
    }
    public Map<String, Object> getPlatformBalance() throws StripeException {
        Balance balance = Balance.retrieve();

        Map<String, Object> result = new HashMap<>();
        result.put("available", balance.getAvailable());
        result.put("pending", balance.getPending());

        return result;
    }

    public Map<String, Object> getBalance(String accountId) throws StripeException {
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(accountId) // ID của connected account
                .build();

        Balance balance = Balance.retrieve(requestOptions);
        Map<String, Object> result = new HashMap<>();
        result.put("available", balance.getAvailable());
        result.put("pending", balance.getPending());
        return result;
    }
}

//package fptu.fcharity.controller;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//import com.stripe.Stripe;
//import com.stripe.exception.SignatureVerificationException;
//import com.stripe.exception.StripeException;
//import com.stripe.model.*;
//import com.stripe.model.checkout.Session;
//import com.stripe.net.Webhook;
//import fptu.fcharity.dto.payment.CreateAccountDto;
//import fptu.fcharity.dto.payment.CreateAccountLinkDto;
//import fptu.fcharity.dto.payment.CreateChargeDto;
//import fptu.fcharity.dto.payment.CreateCheckoutDto;
//import fptu.fcharity.response.payment.CreateCheckoutResponse;
//import fptu.fcharity.helpers.payment.StripeService;
//import fptu.fcharity.response.payment.CreateAccountLinkResponse;
//import fptu.fcharity.response.payment.CreateAccountResponse;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.parameters.P;
//import org.springframework.web.bind.annotation.*;
//
//import static spark.Spark.get;
//import static spark.Spark.port;
//import static spark.Spark.post;
//
//@RestController
//@RequestMapping("/stripe")
//public class StripeController {
//    private final Logger logger = LoggerFactory.getLogger(StripeController.class);
//
//    @Autowired
//    private StripeService stripeService;
//    // Inject giá trị từ application.properties
//    @Value("${stripe.secret.key}")
//    private String secretKey;
//
//    @Value("${stripe.webhook.secret}")
//    private String endpointSecret;
//
//    @PostConstruct
//    public void init() {
//        // Thiết lập API key toàn cục khi ứng dụng khởi động
//        Stripe.apiKey = secretKey;
//        logger.info("Stripe API Key initialized.");
//        if (endpointSecret == null || endpointSecret.isEmpty() || endpointSecret.equals("whsec_...")) {
//            logger.warn("Stripe Webhook Secret is not configured. Please set 'stripe.webhook.secret' in application properties.");
//        } else {
//            logger.info("Stripe Webhook Secret loaded.");
//        }
//    }
//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleStripeWebhook(
//            @RequestBody String payload, // Nhận raw body của request
//            @RequestHeader("Stripe-Signature") String sigHeader) { // Nhận header Stripe-Signature
//
//        if (endpointSecret == null) {
//            logger.error("Webhook secret key is not configured.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook secret not configured.");
//        }
//
//        Event event;
//
//        try {
//            // Xác minh chữ ký và parse event
//            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
//            logger.info("Webhook event received: type={}, id={}", event.getType(), event.getId());
//
//        } catch (JsonSyntaxException e) {
//            // Payload không hợp lệ
//            logger.error("Webhook error - Invalid payload: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
//        } catch (SignatureVerificationException e) {
//            // Chữ ký không hợp lệ
//            logger.error("Webhook error - Invalid signature: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
//        } catch (Exception e) {
//            logger.error("Webhook error - Unexpected exception: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
//        }
//
//        // Xử lý các loại event cụ thể
//        switch (event.getType()) {
//            case "checkout.session.completed":
//                Session session = (Session) event.getData().getObject();
//                logger.info("Checkout session completed session: {}", session);
//                String connectedAccountId = event.getAccount(); // Lấy ID tài khoản Connect (nếu có)
//                logger.info("Processing checkout.session.completed for session ID: {}", session.getId());
//                logger.info("Connected account ID: {}", connectedAccountId);
////                stripeService.handleCompletedCheckoutSession(connectedAccountId, session);
//                break;
//            // Thêm các case cho các loại event khác bạn muốn xử lý
//            // case "payment_intent.succeeded":
//            //     PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
//            //     logger.info("Processing payment_intent.succeeded for PaymentIntent ID: {}", paymentIntent.getId());
//            //     stripeEventService.handleSuccessfulPaymentIntent(paymentIntent);
//            //     break;
//            default:
//                logger.warn("Unhandled event type: {}", event.getType());
//        }
//
//        // Trả về 200 OK cho Stripe để xác nhận đã nhận event
//        return ResponseEntity.ok().build();
//    }
//
//    //get balance
//    @GetMapping("/balance")
//    public ResponseEntity<?>getPlatformBalance() throws StripeException {
//        return ResponseEntity.ok( stripeService.getPlatformBalance());
//    }
//    @GetMapping("/balance/{accountId}")
//    public ResponseEntity<?>getBalance(@PathVariable String accountId) throws StripeException {
//        return ResponseEntity.ok(stripeService.getBalance(accountId));
//    }
//    //charge
//    @PostMapping("/charge")
//    public ResponseEntity<?>createCharge(@RequestBody CreateChargeDto dto) throws StripeException {
//        Charge charge = stripeService.createCharge(dto);
//        System.out.println(charge);
//        return ResponseEntity.ok(charge.getReceiptUrl());
//    }
//    //withdraw
//    @PostMapping("/payout")
//    public ResponseEntity<?> createPayout(@RequestBody fptu.fcharity.dto.payment.CreatePayoutDto dto) throws StripeException {
//        Payout payout = stripeService.createPayout(dto);
//        return ResponseEntity.ok(payout.getId());
//    }
////make payment
//    @PostMapping("/create-price")
//    public ResponseEntity<?> createPrice(@RequestBody fptu.fcharity.dto.payment.PriceDto dto) throws StripeException {
//        Price price = stripeService.createPrice(dto);
//        return ResponseEntity.ok(price.getId());
//    }
//    @PostMapping("/create-payment-link")
//    public ResponseEntity<?> createPaymentLink(@RequestBody fptu.fcharity.dto.payment.CreatePaymentLinkDto dto) throws StripeException {
//        com.stripe.model.PaymentLink paymentLink = stripeService.createPaymentLink(dto);
//        return ResponseEntity.ok(paymentLink.getUrl());
//    }
//
//    @PostMapping("/checkout")
//    public ResponseEntity<CreateCheckoutResponse> checkoutProducts(@RequestBody CreateCheckoutDto req) throws StripeException {
//        CreateCheckoutResponse createCheckoutResponse = stripeService.checkoutProducts(req);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(createCheckoutResponse);
//    }
////transfer donate from project to user
//    @PostMapping("/transfer")
//    public ResponseEntity<Transfer> createTransfer(@RequestBody fptu.fcharity.dto.payment.CreateTransferDto req) throws StripeException {
//        Transfer transfer = stripeService.createTransfer(req);
//        return ResponseEntity.ok(transfer);
//    }
//    @GetMapping("/transfers/{accountId}")
//    public ResponseEntity<TransferCollection> getTransfers(@PathVariable String accountId) throws StripeException {
//        TransferCollection transfers = stripeService.listTransfers(accountId);
//        return ResponseEntity.ok(transfers);
//    }
////connect account
//    @PostMapping("/account")
//    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountDto dto) throws StripeException {
//        CreateAccountResponse response = stripeService.createAccount(dto);
//        return ResponseEntity.ok(response);
//    }
//    @PostMapping("/account-link")
//    public ResponseEntity<CreateAccountLinkResponse> createAccountLink(@RequestBody CreateAccountLinkDto req) throws StripeException {
//        CreateAccountLinkResponse response = stripeService.createAccountLink(req);
//        return ResponseEntity.ok(response);
//    }
//}

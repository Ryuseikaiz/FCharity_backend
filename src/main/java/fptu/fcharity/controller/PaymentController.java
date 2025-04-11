package fptu.fcharity.controller;

import fptu.fcharity.dto.payment.PaymentDto;
import fptu.fcharity.service.manage.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;
    @Autowired
    private UserService userService;

    private String generateRandomLettersAZaz(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return new Random().ints(length, 0, letters.length())
                .mapToObj(letters::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPaymentLink(@RequestBody PaymentDto paymentDto) throws Exception {
        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

        String domain = "http://localhost:3001/user/manage-profile/deposit/"+paymentDto.getUserId(); // bạn có thể thay đổi
        long orderCode = System.currentTimeMillis() / 1000;

        String verificationCode = generateRandomLettersAZaz(24);
        userService.updateVerificationCode(paymentDto.getUserId(), verificationCode);
        ItemData itemData = ItemData.builder()
                .name(paymentDto.getItemContent())
                .quantity(1)
                .price(paymentDto.getAmount())
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(paymentDto.getAmount())
                .description(verificationCode)
                .returnUrl(domain)
                .cancelUrl(domain)
                .item(itemData)
                .build();

        CheckoutResponseData result = payOS.createPaymentLink(paymentData);

        return ResponseEntity.ok(result.getCheckoutUrl());
    }
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        // Parse data từ payload
        // Kiểm tra trạng thái thanh toán
        // Cập nhật vào database: ví dụ cộng tiền vào tài khoản, cập nhật trạng thái đơn hàng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("Webhook received: " + payload);
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        System.out.println("Amount: " + data.get("amount"));
        System.out.println("Description: " + data.get("description"));
        System.out.println("Transaction Status: " + payload.get("desc"));

        ZonedDateTime zonedDateTime = ZonedDateTime.parse((String) data.get("transactionDateTime"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));

        Instant transactionDateTime = zonedDateTime.toInstant();
//        Instant transactionDateTime = localDateTime.toInstant(ZoneOffset.UTC);
        try{
            userService.depositToWallet(
                    (String) data.get("description"),
                    (int) data.get("amount"),
                    transactionDateTime
            );
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return ResponseEntity.ok("Received");
    }

}

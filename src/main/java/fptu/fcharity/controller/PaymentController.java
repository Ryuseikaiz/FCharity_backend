package fptu.fcharity.controller;

import fptu.fcharity.dto.payment.PaymentDto;
import fptu.fcharity.dto.project.ToProjectDonationDto;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.response.project.ToProjectDonationResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.service.manage.project.ProjectService;
import fptu.fcharity.service.manage.project.ToProjectDonationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.constants.project.DonationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.Random;

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
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ToProjectDonationService toProjectDonationService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private HelpNotificationService notificationService;


    private int generateRandomOrderCode() {
        Random random = new Random();
        int orderCode = random.nextInt(900000000) + 1000000000; // Đảm bảo rằng số là dương và có 10 chữ số
        return orderCode;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPaymentLink(@RequestBody PaymentDto paymentDto) throws Exception {
        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

        String domain = "http://localhost:3001/" + paymentDto.getReturnUrl();
        long orderCode = System.currentTimeMillis() / 1000;
        if(paymentDto.getObjectType().equals("PROJECT")) {
            ToProjectDonationDto donationDto = new ToProjectDonationDto();
            donationDto.setProjectId(paymentDto.getObjectId());
            donationDto.setAmount(paymentDto.getAmount());
            donationDto.setMessage(paymentDto.getPaymentContent());
            donationDto.setUserId(paymentDto.getUserId());
            donationDto.setDonationStatus(DonationStatus.PENDING);
            donationDto.setOrderCode(generateRandomOrderCode());
            ToProjectDonationResponse res =  toProjectDonationService.createDonation(donationDto);
            orderCode = res.getOrderCode();
        }

        ItemData itemData = ItemData.builder()
                .name(paymentDto.getItemContent())
                .quantity(1)
                .price(paymentDto.getAmount())
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(paymentDto.getAmount())
                .description(paymentDto.getPaymentContent())
                .returnUrl(domain)
                .cancelUrl(domain)
                .item(itemData)
                .build();

        CheckoutResponseData result = payOS.createPaymentLink(paymentData);

        return ResponseEntity.ok(result.getCheckoutUrl());
    }
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("Webhook received: " + payload);
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        System.out.println("Amount: " + data.get("amount"));
        System.out.println("orderCode: " + data.get("orderCode"));
        System.out.println("Transaction Status: " + payload.get("desc"));

        ZonedDateTime zonedDateTime = ZonedDateTime.parse((String) data.get("transactionDateTime"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        Instant transactionDateTime = zonedDateTime.toInstant();
        try{
           ToProjectDonationResponse p =  toProjectDonationService.updateDonation(
                   (int)data.get("orderCode"),
                    transactionDateTime,
                   DonationStatus.COMPLETED
            );

            System.out.println(p);
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return ResponseEntity.ok("Received");
    }

}

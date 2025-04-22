package fptu.fcharity.controller;

import fptu.fcharity.dto.chat.ChatErrorResponse;
import fptu.fcharity.dto.chat.ChatRequest;
import fptu.fcharity.dto.chat.ChatResponse;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class GeminiChatController {

    private final GeminiChatService geminiChatService;

    @PostMapping("/gemini")
    public ResponseEntity<?> handleChat(@RequestBody ChatRequest chatRequest) {

        String userId = null;
        String userIdentifier = "Anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                userId = user.getId().toString();
                userIdentifier = user.getEmail();
            } else if (principal instanceof UserDetails userDetails) {
                userIdentifier = userDetails.getUsername();
                log.warn("Principal is UserDetails, could not get UUID directly. Using username: {}", userIdentifier);
            } else {
                log.warn("Authenticated principal is not an instance of User or UserDetails: {}", principal.getClass());
                userIdentifier = "AuthenticatedUser(UnknownType)";
            }
        }

        log.info("Received chat request from: {}", userIdentifier);

        if (chatRequest.getMessage() == null || chatRequest.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ChatErrorResponse("Message cannot be empty"));
        }

        try {
            String reply = geminiChatService.generateReply(
                    chatRequest.getMessage(),
                    chatRequest.getHistory(),
                    userId
            );
            return ResponseEntity.ok(new ChatResponse(reply));

        } catch (IOException e) {
            log.error("Chat generation failed (IOException) for user: {}", userIdentifier, e);
            String userFriendlyError = e.getMessage().contains("blocked by") || e.getMessage().contains("Rate limit")
                    ? e.getMessage()
                    : "An error occurred while communicating with the AI assistant.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatErrorResponse(userFriendlyError, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during chat processing for user: {}", userIdentifier, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatErrorResponse("An unexpected server error occurred.", e.getMessage()));
        }
    }
}
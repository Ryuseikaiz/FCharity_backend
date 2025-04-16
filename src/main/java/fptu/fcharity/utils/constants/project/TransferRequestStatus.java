package fptu.fcharity.utils.constants.project;

public class TransferRequestStatus {
        public static final String PENDING_USER_CONFIRM = "PENDING_USER_CONFIRM";
        public static final String PENDING_ADMIN_APPROVAL = "PENDING_ADMIN_APPROVAL";
        public static final String CONFIRM_SENT = "CONFIRM_SENT";
        public static final String COMPLETED = "COMPLETED";
        public static final String ERROR = "ERROR";
/*
* -- Giá trị status đề xuất:
-- 'PENDING_USER_CONFIRM'     -- Chờ requester nhập thông tin ngân hàng
-- 'PENDING_ADMIN_APPROVAL'   -- Chờ admin chuyển tiền
-- 'CONFIRM_SENT'             -- Admin đã gửi tiền, đính kèm ảnh
-- 'COMPLETED'                -- Requester xác nhận đã nhận tiền
-- 'ERROR'                    -- Có sự cố
*
* */
}

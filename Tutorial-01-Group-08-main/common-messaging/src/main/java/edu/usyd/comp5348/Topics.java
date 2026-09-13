package edu.usyd.comp5348;

public class Topics {
    // 外部系统的 Exchange（与各微服务中保持一致）
    public static final String STORE_EXCHANGE = "store.exchange";
    public static final String WAREHOUSE_EXCHANGE = "warehouse.exchange";
    public static final String BANK_EXCHANGE = "bank.exchange";
    public static final String DELIVERY_EXCHANGE = "delivery.exchange";
    public static final String EMAIL_EXCHANGE = "email.exchange";

    // === 来自各服务的“事件” ===
    public static class StoreEvt {
        public static final String ORDER_CREATED = "order.created";
        public static final String ORDER_CANCEL_REQUEST = "order.cancel.request";
    }

    public static class WarehouseEvt {
        public static final String RESERVED = "order.reserved";
        public static final String RELEASED = "order.released";
        public static final String FINALIZED = "order.finalized";
        public static final String OUT_OF_STOCK = "order.out_of_stock";
    }


    public static class BankEvt {
        public static final String PAID = "order.paid";
        public static final String PAYMENT_FAILED = "order.payment_failed";
        public static final String REFUNDED = "order.refunded";
    }

    public static class DeliveryEvt {
        public static final String RECEIVED = "delivery.received";
        public static final String PICKED_UP = "delivery.picked_up";
        public static final String OUT_FOR_DELIVERY = "delivery.out_for_delivery";
        public static final String DELIVERED = "delivery.delivered";
        public static final String FAILED_IN_TRANSIT = "delivery.failed.in_transit";
    }

    // === 下发给各服务的“指令/请求” ===
    public static class WarehouseCmd {
        public static final String RESERVE_REQUEST = "warehouse.reserve.request";
        public static final String RELEASE_REQUEST = "warehouse.release.request";
        public static final String FINALIZE_REQUEST = "warehouse.finalize.request";
        public static final String CHECK_REQUEST = "warehouse.check.request";
    }

    public static class BankCmd {
        public static final String PAYMENT_REQUEST = "bank.payment.request";
        public static final String REFUND_REQUEST = "bank.refund.request";
    }

    public static class DeliveryCmd {
        public static final String REQUESTED = "delivery.requested";
    }

    public static class StoreCmd {
        public static final String ORDER_RESERVED = "order.reserved";
        public static final String ORDER_RELEASED = "order.released";
        public static final String ORDER_PAID = "order.paid";
        public static final String ORDER_DELIVERY_REQUESTED = "order.delivery_requested";
        public static final String ORDER_PAYMENT_FAILED = "order.payment_failed";
        public static final String ORDER_FINALIZED = "order.finalized";
        public static final String ORDER_DELIVERED = "order.delivered";
        public static final String ORDER_CANCEL_REJECTED = "order.cancel_rejected";
        public static final String ORDER_DELIVERY_FAILED = "order.delivery_failed";
    }


    public static class EmailCmd {
        public static final String SEND_ORDER_UPDATE = "email.order.update";
        public static final String SEND_DELIVERY_STATUS = "email.delivery.status";
        public static final String SEND_REFUND_NOTICE = "email.refund.notice";
    }

    public static class EmailEvt {
        public static final String SENT_SUCCESS = "email.sent.success";
        public static final String SENT_FAILED = "email.sent.failed";
    }


}
package com.recykal.audit.constants;

public final class Constants {

    public static final class RABBITMQ_CONSTANTS {

        public static final String AUDIT_RABBITMQ = "audit.rabbitmq";

        public static final String QUEUE = "audit_log_queue";

        public static final String EXCHANGE = "audit_log_topic";

        public static final String ROUTE = "audit_log_route_key";

        public static final String BINDING = "audit_log_binding";

        public static final String MESSAGE_CONVERTER = "audit_log_message_converter";

        public static final String AMQ_TEMPLATE = "audit_log_template";
    }

    public static final class CONFIG_CONSTANTS {

        public static final String AUDIT_OBJECT_MAPPER = "audit_object_mapper";

        public static final String AUDIT = "audit";

        public static final String ENABLED = "enabled";

        public static final String TRUE = "true";
    }
}

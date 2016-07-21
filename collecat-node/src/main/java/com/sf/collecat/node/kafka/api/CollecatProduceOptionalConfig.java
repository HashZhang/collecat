package com.sf.collecat.node.kafka.api;

/**
 * Created by 862911 on 2016/6/27.
 */
public class CollecatProduceOptionalConfig {
    public static CollecatProduceOptionalConfig defaultConfig = new CollecatProduceOptionalConfig();
    private int requestTimeoutMs = 30000;
    private CollecatProduceOptionalConfig.RequestRequiredAck requestRequiredAck;

    public CollecatProduceOptionalConfig() {
        this.requestRequiredAck = CollecatProduceOptionalConfig.RequestRequiredAck.ALL_REPLICA;
    }

    public int getRequestTimeoutMs() {
        return this.requestTimeoutMs;
    }

    public CollecatProduceOptionalConfig.RequestRequiredAck getRequestRequiredAck() {
        return this.requestRequiredAck;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public void setRequestRequiredAck(CollecatProduceOptionalConfig.RequestRequiredAck requestRequiredAck) {
        this.requestRequiredAck = requestRequiredAck;
    }

    static enum RequestRequiredAck {
        NEVER_WAIT(0),
        LEADER_REPLICA(1),
        ALL_REPLICA(-1);

        private int value;

        private RequestRequiredAck(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}

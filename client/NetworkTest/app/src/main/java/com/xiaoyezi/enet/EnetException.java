package com.xiaoyezi.enet;

public class EnetException extends Exception {
    private static final long serialVersionUID = -5753326622210863554L;

    public EnetException() {
        super();
    }

    public EnetException(String message) {
        super(message);
    }

    public EnetException(Throwable cause) {
        super(cause);
    }

    public EnetException(String message, Throwable cause) {
        super(message, cause);
    }
}

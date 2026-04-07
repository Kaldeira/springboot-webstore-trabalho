package com.nami.webstore.exception;

import java.io.Serial;

public class ServerExc extends RuntimeException {
    public ServerExc(String message) {
        super(message);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}

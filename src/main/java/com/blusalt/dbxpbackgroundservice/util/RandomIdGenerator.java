package com.blusalt.dbxpbackgroundservice.util;

import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;

public final class RandomIdGenerator {
    private static final String VALID_CLIENT_ID_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789"; //

    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    public String generateId(int length) {
        SecureRandom ng = Holder.numberGenerator;
        return RandomStringUtils.random(length, 0, VALID_CLIENT_ID_CHARS.length(), false, false,
                VALID_CLIENT_ID_CHARS.toCharArray(), ng);
    }

    public static String generateRandomId(int length) {
        RandomIdGenerator randomIdGenerator = new RandomIdGenerator();
        return randomIdGenerator.generateId(length);
    }
}
package com.connection.domain;

public enum JobTitle {
    DEVELOPER() {
        @Override
        public String returnValue() {
            return DEVELOPER.toString();
        }
    },
    TESTER() {
        @Override
        public String returnValue() {
            return TESTER.toString();
        }
    };

    public abstract String returnValue();
}

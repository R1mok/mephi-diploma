package ru.b19513.pet_manager.exceptions;

public class LoginBusyException extends ServiceException {
    public LoginBusyException(String s) {
        super(s);
    }
}

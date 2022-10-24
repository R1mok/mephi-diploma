package ru.b19513.pet_manager.exceptions;

public class NameTakenException extends ServiceException {
    public NameTakenException(String s) {
        super(s);
    }
}

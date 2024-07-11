package br.com.estudo.testes.exceptions;

public class EmailSavedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailSavedException(String ex) {
        super("Email " + ex + " used by other person.");
    }
}

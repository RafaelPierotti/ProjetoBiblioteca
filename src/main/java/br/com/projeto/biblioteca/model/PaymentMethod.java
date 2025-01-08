package br.com.projeto.biblioteca.model;

public enum PaymentMethod {
    cartaodebito("cartão de débito"),
    cartaocredito("cartão de crédito"),
    dinheito("dinheito"),
    pix("pix");

    private String payment;

    PaymentMethod(String payment){
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "payment='" + payment;
    }
}

package br.com.projeto.biblioteca.model;

public enum PaymentMethod {
    debito("débito"),
    credito("crédito"),
    dinheiro("dinheiro"),
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

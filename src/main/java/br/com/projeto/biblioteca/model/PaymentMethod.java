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

    public static PaymentMethod correctWriting(String text) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()){
            if (paymentMethod.payment.equalsIgnoreCase(text)){
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada: " + text);
    }
}

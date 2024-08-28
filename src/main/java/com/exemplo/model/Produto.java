package com.exemplo.model;

public class Produto {
    private int id;
    private String descricao;
    private double valor;
    private String ean13;
    private String status;
    private double percentualImposto;
    private double valorImposto;

    // Construtores, getters e setters

    public Produto(String descricao, double valor, String ean13, String status, double percentualImposto) {
        this.descricao = descricao;
        this.valor = valor;
        this.ean13 = ean13;
        this.status = status;
        this.percentualImposto = percentualImposto;
        calcularValorImposto();
    }

    public Produto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getEan13() {
        return ean13;
    }

    public void setEan13(String ean13) {
        this.ean13 = ean13;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPercentualImposto() {
        return percentualImposto;
    }

    public void setPercentualImposto(double percentualImposto) {
        this.percentualImposto = percentualImposto;
    }

    public double getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(double valorImposto) {
        this.valorImposto = valorImposto;
    }

    public void calcularValorImposto() {
        this.valorImposto = this.valor * this.percentualImposto / 100;
    }
}

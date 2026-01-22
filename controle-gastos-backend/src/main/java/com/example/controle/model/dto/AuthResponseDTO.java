package com.example.controle.model.dto;

public class AuthResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private String email;
    private String nome;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String email, String nome) {
        this.token = token;
        this.email = email;
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

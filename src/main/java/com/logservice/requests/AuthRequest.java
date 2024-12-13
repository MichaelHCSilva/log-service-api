package com.logservice.requests;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;

public class AuthRequest {

    //NotBlank(message = "O campo 'username' é obrigatório.")
    //@Size(min = 3, max = 50, message = "O 'username' deve ter entre 3 e 50 caracteres.")
    //@Pattern(regexp = "^[a-zA-ZáéíóúàèìòùãõâêîôûçÇ\\s]{3,50}$", message = "O nome de usuário deve conter apenas letras e espaços.")
    private String username;

    //@NotBlank(message = "O campo 'password' é obrigatório.")
    //@Size(min = 6, message = "A 'senha' deve ter pelo menos 6 caracteres.")
    //@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{6,}$", message = "A senha deve conter pelo menos uma letra maiúscula, um número e um caractere especial.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

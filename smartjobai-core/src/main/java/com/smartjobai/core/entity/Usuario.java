package com.smartjobai.core.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_usuario_email", columnList = "email", unique = true)
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Usuario extends EntidadeBase {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha; // hash BCrypt

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    private boolean ativo = true;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private LocalDateTime ultimaSincronizacao;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Curriculo> curriculos = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experiencia> experiencias = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Formacao> formacoes = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Idioma> idiomas = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certificacao> certificacoes = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HabilidadeTecnica> habilidadesTecnicas = new HashSet<>();
}

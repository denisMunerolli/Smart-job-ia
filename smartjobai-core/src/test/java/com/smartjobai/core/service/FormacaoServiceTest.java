package com.smartjobai.core.service;

import com.smartjobai.core.entity.Formacao;
import com.smartjobai.core.entity.NivelFormacao;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class FormacaoServiceTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FormacaoService formacaoService;

    private String email;

    @BeforeEach
    void setUp() {
        email = "formacao.teste+" + System.nanoTime() + "@email.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("123456");
        usuario.setNome("Usuário Teste");
        usuarioService.cadastrar(usuario);
    }

    @Test
    void deveCriarEListarFormacaoDoUsuario() {
        Formacao formacao = new Formacao();
        formacao.setInstituicao("Universidade Exemplo");
        formacao.setCurso("Ciência da Computação");
        formacao.setNivel(NivelFormacao.GRADUACAO);
        formacao.setDataInicio(LocalDate.of(2018, 2, 1));
        formacao.setDataFim(LocalDate.of(2022, 12, 1));

        formacaoService.criar(email, formacao);

        assertThat(formacaoService.listarPorUsuario(email)).hasSize(1);
    }

    @Test
    void naoDevePermitirAcessarFormacaoDeOutroUsuario() {
        Formacao formacao = new Formacao();
        formacao.setInstituicao("Universidade Exemplo");
        formacao.setCurso("Engenharia");
        formacao.setNivel(NivelFormacao.GRADUACAO);
        Formacao criada = formacaoService.criar(email, formacao);

        String outroEmail = "outro.usuario+" + System.nanoTime() + "@email.com";
        Usuario outro = new Usuario();
        outro.setEmail(outroEmail);
        outro.setSenha("123456");
        outro.setNome("Outro Usuário");
        usuarioService.cadastrar(outro);

        assertThatThrownBy(() -> formacaoService.buscarPorId(outroEmail, criada.getId()))
                .isInstanceOf(BusinessException.class);
    }
}

package com.smartjobai.core.service;

import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class UsuarioServiceTest {

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

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario u = new Usuario();
        u.setEmail("teste@email.com");
        u.setSenha("123456");
        u.setNome("Teste");
        Usuario salvo = usuarioService.cadastrar(u);
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getSenha()).isNotEqualTo("123456"); // encriptada
    }

    @Test
    void deveLancarExcecaoAoCadastrarEmailDuplicado() {
        Usuario u1 = new Usuario();
        u1.setEmail("duplicado@email.com");
        u1.setSenha("123456");
        u1.setNome("A");
        usuarioService.cadastrar(u1);

        Usuario u2 = new Usuario();
        u2.setEmail("duplicado@email.com");
        u2.setSenha("456789");
        u2.setNome("B");
        assertThatThrownBy(() -> usuarioService.cadastrar(u2))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já cadastrado");
    }
}

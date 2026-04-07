package com.nami.webstore.repository;

import com.nami.webstore.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select x from Usuario x where x.email = :email")
    public Usuario findByEmail(String email);

    @Query("select x from Usuario x where x.email = :user and x.senhaHash = :senha")
    public Usuario buscarLogin(String user, String senha);
}

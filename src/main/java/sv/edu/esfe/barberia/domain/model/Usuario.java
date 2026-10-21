package sv.edu.esfe.barberia.domain.model;

public record Usuario(Long id, String email, String passwordHash, String rol) {}

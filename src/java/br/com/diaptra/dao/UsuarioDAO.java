package br.com.diaptra.dao;

import br.com.diaptra.model.Usuario;
import br.com.diaptra.util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDAO implements GenericDAO {

    @Override
    public Boolean cadastrar(Object objeto) {
        // Faz o cast do Object genérico para o modelo Usuario
        Usuario usuario = (Usuario) objeto;
        
        Connection conn = Conexao.getConnection();
        PreparedStatement stmt = null;
        
        // Altere os nomes das colunas se no seu banco estiver diferente (ex: nome, email, senha)
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se inseriu com sucesso

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuario no PostgreSQL: " + e.getMessage());
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            Conexao.fecharConexao(conn);
        }
    }

    // Método extra para fazermos o Login mais para frente
    public Usuario autenticar(String email, String senha) {
        Connection conn = Conexao.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;

        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuario: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            Conexao.fecharConexao(conn);
        }

        return usuario;
    }

    // Os outros métodos da interface você pode deixar assim por enquanto, 
    // ou retornar false/null para não dar erro de compilação:

    @Override
    public Boolean inserir(Object objeto) {
        return false;
    }

    @Override
    public Boolean alterar(Object objeto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean excluir(int numero) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object carregar(int numero) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Object> listar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
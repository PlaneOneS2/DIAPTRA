package br.com.diaptra.dao;

import br.com.diaptra.model.Usuario;
import br.com.diaptra.util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class UsuarioDAO implements GenericDAO {

    @Override
    public Boolean cadastrar(Object objeto) {
        Usuario usuario = (Usuario) objeto;
        
        Connection conn = Conexao.getConnection();
        PreparedStatement stmtUsuario = null;
        PreparedStatement stmtEndereco = null;
        ResultSet rsKeys = null;

        String sqlUsuario = "INSERT INTO Usuario (nome, data_nas, email, telefone, senha) VALUES (?, ?, ?, ?, ?)";
        
        String sqlEndereco = "INSERT INTO Endereco (rua, numero, bairro, cidade, estado, complemento, usuarioID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            stmtUsuario.setString(1, usuario.getNome());
            
            if (usuario.getDataNascimento() != null && !usuario.getDataNascimento().isEmpty()) {
                stmtUsuario.setDate(2, java.sql.Date.valueOf(usuario.getDataNascimento()));
            } else {
                stmtUsuario.setNull(2, java.sql.Types.DATE);
            }
            
            stmtUsuario.setString(3, usuario.getEmail());
            stmtUsuario.setString(4, usuario.getTelefone());
            stmtUsuario.setString(5, usuario.getSenha());

            int linhasUsuario = stmtUsuario.executeUpdate();
            
            if (linhasUsuario == 0) {
                throw new SQLException("Falha ao inserir o usuário.");
            }

            // Recupera o id_usuario gerado pelo SERIAL
            rsKeys = stmtUsuario.getGeneratedKeys();
            int idUsuarioGerado = 0;
            if (rsKeys.next()) {
                idUsuarioGerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("Não foi possível obter o ID gerado para o usuário.");
            }

            stmtEndereco = conn.prepareStatement(sqlEndereco);
            stmtEndereco.setString(1, usuario.getRua());
            
            int numInt = 0;
            try {
                numInt = Integer.parseInt(usuario.getNumero());
            } catch (NumberFormatException e) {
                System.out.println("Aviso: Número inválido enviado, salvando como 0.");
            }
            stmtEndereco.setInt(2, numInt);
            
            stmtEndereco.setString(3, usuario.getBairro());
            stmtEndereco.setString(4, usuario.getCidade());
            stmtEndereco.setString(5, usuario.getEstado());
            stmtEndereco.setString(6, usuario.getComplemento());
            stmtEndereco.setInt(7, idUsuarioGerado); 

            stmtEndereco.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Erro ao cadastrar usuario e endereço no PostgreSQL: " + e.getMessage());
            return false;
        } finally {
            // Fecha todos os recursos abertos
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception e) {}
            try { if (stmtUsuario != null) stmtUsuario.close(); } catch (Exception e) {}
            try { if (stmtEndereco != null) stmtEndereco.close(); } catch (Exception e) {}
            Conexao.fecharConexao(conn);
        }
    }
    
    @Override
    public Usuario autenticar(String email, String senha) {
        Connection conn = Conexao.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;

        String sql = "SELECT * FROM Usuario WHERE email = ? AND senha = ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario")); 
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefone(rs.getString("telefone"));
                usuario.setDataNascimento(rs.getString("data_nas"));
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

    @Override
    public Boolean inserir(Object objeto) { return false; }

    @Override
    public Boolean alterar(Object objeto) {
        Usuario usuario = (Usuario) objeto;
        Connection conn = Conexao.getConnection();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE Usuario SET nome = ?, email = ?, telefone = ? WHERE id_usuario = ?";
        
        try{
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setInt(4, usuario.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e){
            System.out.println("Erro ao alterar usuario no PostgreSQL: " + e.getMessage());
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            Conexao.fecharConexao(conn);
        }
        
    }

    @Override
    public Boolean excluir(int numero) { throw new UnsupportedOperationException("Not supported."); }

    @Override
    public Object carregar(int numero) { throw new UnsupportedOperationException("Not supported."); }

    @Override
    public List<Object> listar() { throw new UnsupportedOperationException("Not supported."); }
}
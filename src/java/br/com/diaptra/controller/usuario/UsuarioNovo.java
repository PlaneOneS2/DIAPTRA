package br.com.diaptra.controller.usuario;

import br.com.diaptra.dao.UsuarioDAO;
import br.com.diaptra.model.Usuario;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioNovo", urlPatterns = {"/UsuarioNovo"})
public class UsuarioNovo extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json;charset=UTF-8");

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String linha;
        while ((linha = reader.readLine()) != null) {
            buffer.append(linha);
        }
        
        String jsonRecebido = buffer.toString();

        try {
            
            String nome = extrairValorJson(jsonRecebido, "nome");
            String email = extrairValorJson(jsonRecebido, "email");
            String senha = extrairValorJson(jsonRecebido, "senha");

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Campos obrigatorios vazios.\"}");
                return;
            }

            Usuario oUsuario = new Usuario();
            oUsuario.setNome(nome);
            oUsuario.setEmail(email);
            oUsuario.setSenha(senha);
            
            // ---> CAMPOS NOVOS ADICIONADOS AQUI <---
            oUsuario.setTelefone(extrairValorJson(jsonRecebido, "telefone"));
            oUsuario.setDataNascimento(extrairValorJson(jsonRecebido, "dataNascimento"));
            oUsuario.setRua(extrairValorJson(jsonRecebido, "rua"));
            oUsuario.setNumero(extrairValorJson(jsonRecebido, "numero"));
            oUsuario.setBairro(extrairValorJson(jsonRecebido, "bairro"));
            oUsuario.setCidade(extrairValorJson(jsonRecebido, "cidade"));
            oUsuario.setEstado(extrairValorJson(jsonRecebido, "estado"));
            oUsuario.setComplemento(extrairValorJson(jsonRecebido, "complemento"));

            UsuarioDAO dao = new UsuarioDAO();
            
            boolean cadastrou = dao.cadastrar(oUsuario); 

            if (cadastrou) {
                response.getWriter().write("{\"status\": \"sucesso\", \"mensagem\": \"Estudante cadastrado com sucesso!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Nao foi possivel salvar no banco de dados.\"}");
            }

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Erro no servidor: " + ex.getMessage() + "\"}");
            ex.printStackTrace();
        }
    }

    private String extrairValorJson(String json, String chave) {
        try {
            int indexChave = json.indexOf("\"" + chave + "\"");
            if (indexChave == -1) return "";
            int indexDoisPontos = json.indexOf(":", indexChave);
            int indexInicioValor = json.indexOf("\"", indexDoisPontos);
            int indexFimValor = json.indexOf("\"", indexInicioValor + 1);
            return json.substring(indexInicioValor + 1, indexFimValor);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author ferreira
 */
@WebServlet(name = "UsuarioAlterar", urlPatterns = {"/UsuarioAlterar"})
public class UsuarioAlterar extends HttpServlet{

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
            String idStr = extrairValorJson(jsonRecebido, "id");
            String nome = extrairValorJson(jsonRecebido, "nome");
            String email = extrairValorJson(jsonRecebido, "email");
            String telefone = extrairValorJson(jsonRecebido, "telefone");

            if (idStr.isEmpty() || nome.isEmpty() || email.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Dados insuficientes.\"}");
                return;
            }

            Usuario oUsuario = new Usuario();
            oUsuario.setId(Integer.parseInt(idStr)); 
            oUsuario.setNome(nome);
            oUsuario.setEmail(email);
            oUsuario.setTelefone(telefone);

            UsuarioDAO dao = new UsuarioDAO();
            boolean alterou = dao.alterar(oUsuario);

            if (alterou) {
                response.getWriter().write("{\"status\": \"sucesso\", \"mensagem\": \"Perfil atualizado!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Erro ao atualizar no banco.\"}");
            }

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Erro no servidor.\"}");
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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
}

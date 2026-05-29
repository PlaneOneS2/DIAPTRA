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

@WebServlet(name = "UsuarioLogin", urlPatterns = {"/UsuarioLogin"})
public class UsuarioLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Permissões CORS (Sem isso o React não consegue conectar)
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
            String email = extrairValorJson(jsonRecebido, "email");
            String senha = extrairValorJson(jsonRecebido, "senha");

            UsuarioDAO dao = new UsuarioDAO();
            
            // Chama o método autenticar que criamos na DAO
            Usuario usuarioValidado = dao.autenticar(email, senha);

            if (usuarioValidado != null) {
                // Se achou no banco, monta o JSON devolvendo os dados dele!
                String jsonResposta = String.format(
                    "{\"status\": \"sucesso\", \"usuario\": {\"nome\": \"%s\", \"email\": \"%s\", \"telefone\": \"%s\"}}",
                    usuarioValidado.getNome(), usuarioValidado.getEmail(), usuarioValidado.getTelefone()
                );
                response.getWriter().write(jsonResposta);
            } else {
                // Se não achou (senha ou e-mail errados)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"E-mail ou senha incorretos.\"}");
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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Libera o "pre-flight" do CORS para o navegador
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
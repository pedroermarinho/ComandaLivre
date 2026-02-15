package io.github.pedroermarinho.comandalivreapi.shared.core.infra.util

object EmailTemplate {
    /**
     * Gera um corpo de e-mail HTML com um template padrão, envolvendo o conteúdo principal fornecido.
     *
     * @param title O título que aparecerá no cabeçalho do e-mail.
     * @param mainContentHtml O conteúdo HTML principal e específico para este e-mail.
     * @return Uma String contendo o HTML completo do e-mail.
     */
    fun create(
        title: String,
        mainContentHtml: String,
    ): String =
        """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    width: 100%;
                    max-width: 600px;
                    margin: 0 auto;
                    background-color: #ffffff;
                    border-radius: 8px;
                    overflow: hidden;
                    border: 1px solid #dddddd;
                }
                .header {
                    background-color: #4A90E2; /* Cor azul do Comanda Livre (exemplo) */
                    color: #ffffff;
                    padding: 20px;
                    text-align: center;
                }
                .header h1 {
                    margin: 0;
                    font-size: 24px;
                }
                .content {
                    padding: 30px;
                    color: #333333;
                    line-height: 1.6;
                }
                .footer {
                    background-color: #f4f4f4;
                    color: #888888;
                    padding: 20px;
                    text-align: center;
                    font-size: 12px;
                }
                .footer a {
                    color: #4A90E2;
                    text-decoration: none;
                }
                .button {
                    display: inline-block;
                    background-color: #007bff;
                    color: white !important; /* Importante para sobrescrever estilos de link do cliente de e-mail */
                    padding: 12px 25px;
                    text-align: center;
                    text-decoration: none;
                    border-radius: 5px;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td align="center" style="padding: 20px 0;">
                        <div class="container">
                            <div class="header">
                                <h1>$title</h1>
                            </div>
                            <div class="content">
                                $mainContentHtml
                            </div>
                            <div class="footer">
                                <p>&copy; ${java.time.Year.now()} Comanda Livre. Todos os direitos reservados.</p>
                                <p>Se você recebeu este e-mail por engano, por favor, ignore-o.</p>
                                <p><a href="https://comandalivre.com.br">Nosso Site</a> | <a href="https://comandalivre.com.br/suporte">Suporte</a></p>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.trimIndent()
}

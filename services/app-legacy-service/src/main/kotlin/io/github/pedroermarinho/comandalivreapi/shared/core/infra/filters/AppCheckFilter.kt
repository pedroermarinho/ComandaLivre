package io.github.pedroermarinho.comandalivreapi.shared.core.infra.filters

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import io.github.pedroermarinho.shared.exceptions.UnauthorizedException
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.AppCheckProperties
import jakarta.annotation.PostConstruct
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URI
import java.time.Instant

/**
 * Filtro responsável por validar tokens do **Firebase App Check** em cada requisição HTTP.
 *
 * - O App Check protege o backend contra acessos não autorizados de apps falsos.
 * - O token é enviado no header `X-Firebase-AppCheck`.
 * - Este filtro valida assinatura, issuer, audience, expiração e subject.
 * - Pode ser habilitado/desabilitado via propriedade de configuração.
 */
@Component
class AppCheckFilter(
    private val appCheckProperties: AppCheckProperties,
) : OncePerRequestFilter() {
    // Logger para registrar eventos importantes
    private val log = LoggerFactory.getLogger(AppCheckFilter::class.java)

    // Processador de JWT configurado com as regras de validação
    private val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()

    init {
        // Configura um "retriever" com timeout para buscar JWKS remotamente
        val resourceRetriever = DefaultResourceRetriever(2000, 2000)

        // Cria uma fonte de chaves (JWKSource) que baixa as chaves públicas do Firebase
        val keySource =
            JWKSourceBuilder
                .create<SecurityContext>(URI(appCheckProperties.jwksUrl).toURL(), resourceRetriever)
                .build()

        // Define o algoritmo de assinatura esperado (RS256)
        val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource)

        // Registra o keySelector no processador JWT
        jwtProcessor.jwsKeySelector = keySelector
    }

    /**
     * Método executado após construção do bean.
     * Loga se o filtro está ativo ou desabilitado.
     */
    @PostConstruct
    fun initLog() {
        if (appCheckProperties.enabled) {
            log.info("✅ AppCheckFilter está ATIVO (validação de App Check habilitada).")
        } else {
            log.warn("⚠️ AppCheckFilter está DESABILITADO (nenhum token será validado).")
        }
    }

    /**
     * Executa a validação do token em cada requisição.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // Se o filtro estiver desabilitado, apenas segue a requisição
        if (!appCheckProperties.enabled) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Extrai o token do header customizado
            val token = request.getHeader("X-Firebase-AppCheck")

            // Se não houver token → 401 Unauthorized
            if (token.isNullOrBlank()) {
                log.warn("Sem X-Firebase-AppCheck: ${request.method} ${request.requestURI}")
                throw UnauthorizedException("Token de checagem de aplicativo ausente")
            }

            // Faz o parse do JWT (sem validação ainda)
            val signedJWT = SignedJWT.parse(token)

            // Verifica se o algoritmo do header é RS256
            val alg = signedJWT.header.algorithm
            if (alg != JWSAlgorithm.RS256) {
                log.warn("Algoritmo inválido no App Check: $alg")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // Processa e valida assinatura + extrai claims
            val claimsSet = jwtProcessor.process(signedJWT, null)

            // 1. Valida issuer
            val expectedIssuer = "https://firebaseappcheck.googleapis.com/${appCheckProperties.projectNumber}"
            if (claimsSet.issuer != expectedIssuer) {
                log.warn("Issuer inválido. esperado=$expectedIssuer, got=${claimsSet.issuer}")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // 2. Valida audience (pode ser projectNumber, projectId ou projects/{projectNumber})
            val audOk =
                claimsSet.audience.any {
                    it == "projects/${appCheckProperties.projectNumber}" || it == appCheckProperties.projectNumber || it == appCheckProperties.projectId
                }
            if (!audOk) {
                log.warn("Audience inválido: ${claimsSet.audience}")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // 3. Valida expiração
            if (claimsSet.expirationTime?.toInstant()?.isBefore(Instant.now()) == true) {
                log.warn("Token expirado em: ${claimsSet.expirationTime}")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // 4. Valida iat (não pode ser muito no futuro)
            val iat = claimsSet.issueTime?.toInstant()
            if (iat != null && iat.isAfter(Instant.now().plusSeconds(30))) {
                log.warn("Token iat no futuro: $iat")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // 5. Valida subject (não pode ser vazio)
            if (claimsSet.subject.isNullOrBlank()) {
                log.warn("Token sem subject")
                throw UnauthorizedException("Seu dispositivo pode não ser seguro")
            }

            // Se passou em todas as validações, armazena as claims na request
            request.setAttribute("appCheckClaims", claimsSet)

            // Continua o fluxo normal da requisição
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            // Qualquer falha de parsing/validação → rejeita o token
            log.warn("Falha validação App Check", ex)
            throw UnauthorizedException("Seu dispositivo pode não ser seguro")
        }
    }

    /**
     * Define rotas que não precisam passar pelo filtro.
     * Exemplo: endpoints de monitoramento/saúde do sistema.
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/actuator") || path.startsWith("/health")
    }
}

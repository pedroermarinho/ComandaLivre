package io.github.pedroermarinho.comandalivreapi.stories

import com.github.f4b6a3.uuid.UuidCreator
import com.github.javafaker.Faker
import io.github.pedroermarinho.comandalivreapi.annotations.DevelopmentTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.order.OrderItemsCreateForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command.CommandRequestForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.order.OrderCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product.ProductCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.TableCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.ChangeCommandTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.ChangeStatusCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.CreateCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.AddOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.CreateProductUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.SearchProductCategoryUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.SearchProductUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.CreateTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.RoleTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.employee.EmployeeInviteRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.CreateCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchTypeCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.ChangeStatusEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.CreateEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserAuthDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CreateUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@SpringBootTest
@DevelopmentTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class RestaurantOnboardingStoryTest {
    @MockitoBean
    private lateinit var currentUserService: CurrentUserService

    @Autowired
    private lateinit var createUserUseCase: CreateUserUseCase

    @Autowired
    private lateinit var createCompanyUseCase: CreateCompanyUseCase

    @Autowired
    private lateinit var createProductUseCase: CreateProductUseCase

    @Autowired
    private lateinit var searchProductCategoryUseCase: SearchProductCategoryUseCase

    @Autowired
    private lateinit var searchTypeCompanyUseCase: SearchTypeCompanyUseCase

    @Autowired
    private lateinit var createTableUseCase: CreateTableUseCase

    @Autowired
    private lateinit var createEmployeeInviteUseCase: CreateEmployeeInviteUseCase

    @Autowired
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @Autowired
    private lateinit var changeStatusEmployeeInviteUseCase: ChangeStatusEmployeeInviteUseCase

    @Autowired
    private lateinit var searchEmployeeUseCase: SearchEmployeeUseCase

    @Autowired
    private lateinit var createCommandUseCase: CreateCommandUseCase

    @Autowired
    private lateinit var addOrderUseCase: AddOrderUseCase

    @Autowired
    private lateinit var changeStatusCommandUseCase: ChangeStatusCommandUseCase

    @Autowired
    private lateinit var searchTableUseCase: SearchTableUseCase

    @Autowired
    private lateinit var searchProductUseCase: SearchProductUseCase

    @Autowired
    private lateinit var searchCommandUseCase: SearchCommandUseCase

    @Autowired
    private lateinit var changeCommandTableUseCase: ChangeCommandTableUseCase

    @Autowired
    private lateinit var searchOrderUseCase: SearchOrderUseCase

    @Autowired
    private lateinit var searchUserUseCase: SearchUserUseCase

    @Autowired
    private lateinit var searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase

    private val faker = Faker(Locale.forLanguageTag("pt-BR"))
    private val ownerUser: UserAuthDTO =
        UserAuthDTO(
            sub = "auth-sub-${UuidCreator.getTimeOrderedEpoch()}",
            name = faker.name().fullName(),
            email = faker.bothify("?????###@comandalivretest.com"),
            picture = null,
            emailVerified = true,
        )
    private lateinit var companyType: CompanyTypeDTO
    private lateinit var createdCompany: EntityId
    private lateinit var waiterRole: RoleTypeDTO
    private lateinit var createdInvite: EmployeeInviteDTO
    private lateinit var inviteeUser: UserDTO
    private lateinit var createdCommand: CommandDTO

    @BeforeEach
    fun setup() {
        whenever(currentUserService.getLoggedUser()).thenReturn(Result.success(ownerUser))
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve criar um novo usuário proprietário")
    fun `etapa 1 - deve criar um novo usuario proprietario`() {
        val result = createUserUseCase.execute(ownerUser)
        result.onFailure { it.printStackTrace() }
        val createdUser = result.getOrThrow()
        assertThat(createdUser).isNotNull
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Proprietário deve criar um restaurante")
    fun `etapa 2 - proprietario deve criar um restaurante`() {
        companyType = searchTypeCompanyUseCase.getByEnum(CompanyTypeEnum.RESTAURANT).getOrThrow()

        val companyForm =
            CompanyCreateRequest(
                name = faker.company().name(),
                email = faker.bothify("?????###@comandalivretest.com"),
                phone = faker.phoneNumber().cellPhone(),
                cnpj = faker.number().digits(14),
                description = faker.lorem().sentence(10),
                typeId = companyType.id.publicId,
            )

        val result = createCompanyUseCase.create(companyForm)

        assertThat(result.isSuccess).isTrue()
        createdCompany = result.getOrThrow()

        assertThat(createdCompany).isNotNull
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve ser possível adicionar 30 produtos de categorias variadas ao restaurante recém-criado")
    fun `etapa 3 - deve adicionar multiplos produtos ao restaurante`() {
        val categoryKeys =
            listOf(
                "appetizers",
                "main_courses_meat",
                "main_courses_fish_seafood",
                "main_courses_pasta",
                "vegetarian_vegan",
                "salads",
                "sandwiches_burgers",
                "pizzas",
                "side_dishes",
                "desserts",
                "non_alcoholic_beverages",
                "alcoholic_beverages_beer_wine",
                "alcoholic_beverages_spirits_cocktails",
                "coffees_teas",
                "kids_menu",
            )

        val categories =
            categoryKeys.mapNotNull { key ->
                searchProductCategoryUseCase.getByKey(key).getOrNull()
            }

        assertThat(categories).isNotEmpty()
        println("Categorias carregadas: ${categories.size}")

        val numberOfProductsToCreate = 30
        var successfulCreations = 0

        repeat(numberOfProductsToCreate) { index ->
            val randomCategory = categories.random()

            val form =
                ProductCreateRequest(
                    name = "${faker.food().dish()} #${index + 1}",
                    price = faker.number().randomDouble(2, 10, 200).toBigDecimal(),
                    description = faker.lorem().sentence(faker.number().numberBetween(5, 15)),
                    ingredients = listOf(faker.food().ingredient(), faker.food().ingredient(), faker.food().ingredient(), faker.food().ingredient()),
                    servesPersons = faker.number().numberBetween(1, 4),
                    companyId = createdCompany.publicId,
                    categoryId = randomCategory.id.publicId,
                    availability = faker.bool().bool(),
                )

            val result = createProductUseCase.create(form)

            assertThat(result.isSuccess)
                .withFailMessage("Falha ao criar produto #${index + 1} ('${form.name}') na categoria '${randomCategory.name}'")
                .isTrue()
            val createdProductId = result.getOrThrow()

            val createdProduct = searchProductUseCase.getById(createdProductId.internalId).getOrThrow()

            assertThat(createdProduct).isNotNull
            assertThat(createdProduct.name).isEqualTo(form.name)
            assertThat(createdProduct.price.compareTo(form.price)).isZero()
            assertThat(createdProduct.description).isEqualTo(form.description)
            assertThat(createdProduct.servesPersons).isEqualTo(form.servesPersons)

            assertThat(createdProduct.category.name).isEqualTo(randomCategory.name)
            assertThat(createdProduct.category.key).isEqualTo(randomCategory.key)

            if (result.isSuccess) successfulCreations++
        }

        println("$successfulCreations de $numberOfProductsToCreate produtos foram criados com sucesso no restaurante '${createdCompany.internalId}'.")
        assertThat(successfulCreations).isEqualTo(numberOfProductsToCreate)
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve ser possível adicionar várias mesas ao restaurante recém-criado")
    fun `etapa 4 - deve adicionar varias mesas ao restaurante`() {
        val numberOfTablesToCreate = 30
        var successfulCreations = 0

        repeat(numberOfTablesToCreate) { index ->
            val form =
                TableCreateRequest(
                    name = "Mesa ${index + 1}",
                    numPeople = faker.number().numberBetween(2, 8),
                    description = "Mesa com vista para ${faker.address().streetName()}",
                    companyId = createdCompany.publicId,
                )

            val result = createTableUseCase.create(form)

            assertThat(result.isSuccess)
                .withFailMessage("Falha ao criar a mesa #${index + 1}")
                .isTrue()

            val createdTable = result.getOrThrow()

            assertThat(createdTable).isNotNull

            if (result.isSuccess) successfulCreations++
        }

        println("$successfulCreations de $numberOfTablesToCreate mesas foram criadas com sucesso para o restaurante '${createdCompany.internalId}'.")
        assertThat(successfulCreations).isEqualTo(numberOfTablesToCreate)
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Proprietário deve convidar um novo funcionário para o restaurante")
    fun `etapa 5 - deve convidar um novo funcionario`() {
        val inviteeAuthData =
            UserAuthDTO(
                sub = "invitee-sub-${UUID.randomUUID()}",
                name = faker.name().fullName(),
                email = faker.bothify("?????###@comandalivretest.com"),
                picture = null,
                emailVerified = true,
            )

        val inviteeUserId = createUserUseCase.execute(inviteeAuthData).getOrThrow()

        inviteeUser = searchUserUseCase.getById(inviteeUserId.internalId).getOrThrow()

        waiterRole = searchRoleTypeUseCase.getByEnum(RoleTypeEnum.WAITER).getOrThrow()

        val inviteForm =
            EmployeeInviteRequest(
                email = inviteeUser.email,
                roleId = waiterRole.id.publicId,
                companyId = createdCompany.publicId,
            )
        val result = createEmployeeInviteUseCase.create(inviteForm)

        assertThat(result.isSuccess).isTrue()
        val createdInviteId = result.getOrThrow()
        createdInvite = searchEmployeeInviteUseCase.getById(createdInviteId.internalId).getOrThrow()
        assertThat(createdInvite.status.key).isEqualTo(EmployeeInviteEnum.PENDING.value)
        println("Convite enviado para '${inviteeUser.name}'. Token: ${createdInvite.token}")
    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Funcionário convidado deve aceitar o convite")
    fun `etapa 6 - funcionario deve aceitar o convite`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val result = changeStatusEmployeeInviteUseCase.changeStatus(createdInvite.id.publicId, EmployeeInviteEnum.ACCEPTED)
        assertThat(result.isSuccess).isTrue()

        val employees = searchEmployeeUseCase.getMyEmployees(PageableDTO()).getOrThrow()

        assertThat(employees.content).isNotEmpty()
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Garçom deve criar uma nova comanda para uma mesa")
    fun `etapa 7 - deve criar uma nova comanda`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val tables = searchTableUseCase.getAll(PageableDTO(), createdCompany.publicId).getOrThrow()
        assertThat(tables.content).isNotEmpty()
        val firstTable = tables.content.first()

        val employees = searchEmployeeUseCase.getAll(PageableDTO(), createdCompany.publicId).getOrThrow()
        val waiterEmployee = employees.content.find { it.user.id.publicId == inviteeUser.id.publicId }
        assertThat(waiterEmployee).isNotNull

        val commandForm =
            CommandRequestForm(
                name = faker.name().fullName(),
                numberOfPeople = 2,
                tableId = firstTable.id.publicId,
                employeeId = waiterEmployee!!.id.publicId,
            )

        val result = createCommandUseCase.create(commandForm)

        result.onFailure {
            it.printStackTrace()
        }
        assertThat(result.isSuccess).isTrue()
        val createdCommandId = result.getOrThrow()
        createdCommand = searchCommandUseCase.getById(createdCommandId.internalId).getOrThrow()
        assertThat(createdCommand.name).isEqualTo(commandForm.name)
        assertThat(createdCommand.status.key).isEqualTo(CommandStatusEnum.OPEN.value)
        println("Comanda '${createdCommand.name}' criada com sucesso.")
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Garçom deve adicionar produtos à comanda")
    fun `etapa 8 - deve adicionar produtos a comanda`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val products = searchProductUseCase.getByCompany(PageableDTO(pageSize = 10), createdCompany.publicId).getOrThrow().content
        assertThat(products.size).isGreaterThanOrEqualTo(10)
        assertThat(products).isNotEmpty()
        val productsToAdd = products.take(10)

        val orderItemsForms =
            productsToAdd.map { product ->
                OrderItemsCreateForm(
                    productId = product.id.publicId,
                    notes = faker.lorem().sentence(10),
                )
            }

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )

        val result = addOrderUseCase.add(orderForm)
        result.onFailure { it.printStackTrace() }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    @Order(9)
    @DisplayName("Etapa 9: O cliente deve pagar a comanda")
    fun `etapa 9 - o cliente deve pagar a comanda`() {
        val result = changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.PAYING)
        result.onFailure { it.printStackTrace() }
        assertThat(result.isSuccess).isTrue()

        val command = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(command.status.key).isEqualTo(CommandStatusEnum.PAYING.value)
    }

    @Test
    @Order(10)
    @DisplayName("Etapa 10: Garçom deve fechar a comanda")
    fun `etapa 10 - deve fechar a comanda`() {
        val result = changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.CLOSED, true)
        result.onFailure { it.printStackTrace() }
        assertThat(result.isSuccess).isTrue()

        val command = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(command.status.key).isEqualTo(CommandStatusEnum.CLOSED.value)
    }

    @Test
    @Order(11)
    @DisplayName("Etapa 11: Proprietário deve ser capaz de reabrir uma comanda fechada")
    fun `etapa 11 - deve reabrir uma comanda fechada`() {
        val commandBeforeReopen = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(commandBeforeReopen.status.key).isEqualTo(CommandStatusEnum.CLOSED.value)

        val result = changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.OPEN)

        assertThat(result.isSuccess)
            .withFailMessage("Falha ao tentar reabrir a comanda.")
            .isTrue()

        val reopenedCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(reopenedCommand.status.key).isEqualTo(CommandStatusEnum.OPEN.value)
    }

    @Test
    @Order(12)
    @DisplayName("Etapa 12: Garçom deve ser capaz de trocar a comanda de mesa")
    fun `etapa 12 - garcom deve ser capaz de trocar a comanda de mesa`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val tables = searchTableUseCase.getAll(PageableDTO(), createdCompany.publicId).getOrThrow().content
        assertThat(tables).isNotEmpty()

        val currentTable = createdCommand.table
        val newTable = tables.first { it.id.publicId != currentTable.id.publicId } // Get a different table

        val result = changeCommandTableUseCase.execute(createdCommand.id.publicId, newTable.id.publicId)
        assertThat(result.isSuccess).isTrue()

        val updatedCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(updatedCommand.table.id.publicId).isEqualTo(newTable.id.publicId)
        println("Comanda '${updatedCommand.name}' trocada para a mesa '${newTable.name}' com sucesso.")
    }

    @Test
    @Order(15)
    @DisplayName("Etapa 15: Garçom não deve ser capaz de trocar a comanda para a mesma mesa")
    fun `etapa 15 - garcom nao deve ser capaz de trocar a comanda para a mesma mesa`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val currentCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        val currentTable = currentCommand.table

        val result = changeCommandTableUseCase.execute(createdCommand.id.publicId, currentTable.id.publicId)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(BusinessLogicException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("A comanda já está na mesa de destino.")
    }

    @Test
    @Order(16)
    @DisplayName("Etapa 16: Garçom não deve ser capaz de trocar a comanda para uma mesa de outra empresa")
    fun `etapa 16 - garcom nao deve ser capaz de trocar a comanda para uma mesa de outra empresa`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        // Create a dummy company and table for another company
        val otherCompanyType = searchTypeCompanyUseCase.getByEnum(CompanyTypeEnum.RESTAURANT).getOrThrow()
        val otherCompanyForm =
            CompanyCreateRequest(
                name = faker.company().name(),
                email = faker.bothify("?????###@othercompany.com"),
                phone = faker.phoneNumber().cellPhone(),
                cnpj = faker.number().digits(14),
                description = faker.lorem().sentence(10),
                typeId = otherCompanyType.id.publicId,
            )
        val otherCompany: EntityId = createCompanyUseCase.create(otherCompanyForm).getOrThrow()

        val otherTableForm =
            TableCreateRequest(
                name = "Mesa de Outra Empresa",
                numPeople = 4,
                description = "Mesa de teste para outra empresa",
                companyId = otherCompany.publicId,
            )
        val otherTable = createTableUseCase.create(otherTableForm).getOrThrow()

        val result = changeCommandTableUseCase.execute(createdCommand.id.publicId, otherTable.publicId)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(BusinessLogicException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("A comanda e a mesa de destino devem pertencer à mesma empresa.")
    }

    @Test
    @Order(17)
    @DisplayName("Etapa 17: Garçom não deve ser capaz de trocar a comanda de mesa se a comanda não estiver aberta")
    fun `etapa 17 - garcom nao deve ser capaz de trocar a comanda de mesa se a comanda nao estiver aberta`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.PAYING).getOrThrow()
        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.CLOSED).getOrThrow()
        val closedCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()

        val tables = searchTableUseCase.getAll(PageableDTO(), createdCompany.publicId).getOrThrow().content
        assertThat(tables).isNotEmpty()

        val currentTable = closedCommand.table
        val newTable = tables.first { it.id.publicId != currentTable.id.publicId } // Get a different table

        val result = changeCommandTableUseCase.execute(closedCommand.id.publicId, newTable.id.publicId)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(BusinessLogicException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("A comanda deve estar aberta para ter sua mesa alterada.")
    }

    @Test
    @Order(18)
    @DisplayName("Etapa 18: Proprietário não deve ser capaz de reabrir uma comanda já aberta")
    fun `etapa 18 - nao deve ser possivel reabrir uma comanda ja aberta`() {
        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.OPEN)
        val commandBeforeReopen = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        assertThat(commandBeforeReopen.status.key).isEqualTo(CommandStatusEnum.OPEN.value)

        val result = changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.OPEN)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(BusinessLogicException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Transição de status de 'open' para 'open' não é permitida.")
    }

    @Test
    @Order(19)
    @DisplayName("Etapa 19: Garçom deve ser capaz de adicionar mais produtos à comanda aberta")
    fun `etapa 19 - garcom deve ser capaz de adicionar mais produtos a comanda aberta`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        createdCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()
        println("Command status after setting to OPEN: ${createdCommand.status.key}")

        val initialOrdersPage =
            searchOrderUseCase
                .getAll(
                    PageableDTO(pageSize = 100),
                    OrderFilterDTO(
                        commandPublicId = createdCommand.id.publicId,
                    ),
                ).getOrThrow()
        val initialOrderCount = initialOrdersPage.content.size
        println("Initial order count: $initialOrderCount")

        val products = searchProductUseCase.getByCompany(PageableDTO(pageSize = 5), createdCompany.publicId).getOrThrow().content
        assertThat(products).isNotEmpty()
        val productsToAdd = products.take(2) // Add 2 more products
        println("Products to add: ${productsToAdd.map { it.name }}")

        val orderItemsForms =
            productsToAdd.map { product ->
                OrderItemsCreateForm(
                    productId = product.id.publicId,
                    notes = faker.lorem().sentence(5),
                )
            }

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )
        println("Order form created: $orderForm")

        println("Command status before adding orders: ${createdCommand.status.key}")
        val addOrderResult = addOrderUseCase.add(orderForm)
        addOrderResult.onFailure { it.printStackTrace() }
        println("Add order result failure message: ${addOrderResult.exceptionOrNull()?.message}")
        assertThat(addOrderResult.isSuccess).isTrue()

        val updatedOrdersPage =
            searchOrderUseCase
                .getAll(
                    PageableDTO(pageSize = 100),
                    OrderFilterDTO(
                        commandPublicId = createdCommand.id.publicId,
                    ),
                ).getOrThrow()
        assertThat(updatedOrdersPage.content.size).isGreaterThan(initialOrderCount)
        println("Mais produtos adicionados à comanda '${createdCommand.name}'. Total de itens: ${updatedOrdersPage.content.size}")
    }

    @Test
    @Order(20)
    @DisplayName("Etapa 20: Garçom não deve ser capaz de adicionar produtos a uma comanda fechada")
    fun `etapa 20 - garcom nao deve ser capaz de adicionar produtos a uma comanda fechada`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        // Ensure the command is closed
        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.PAYING).getOrThrow()
        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.CLOSED, true).getOrThrow()

        val products = searchProductUseCase.getByCompany(PageableDTO(pageSize = 1), createdCompany.publicId).getOrThrow().content
        assertThat(products).isNotEmpty()
        val productToAdd = products.first()

        val orderItemsForms =
            listOf(
                OrderItemsCreateForm(
                    productId = productToAdd.id.publicId,
                    notes = faker.lorem().sentence(5),
                ),
            )

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )

        val exception =
            assertThrows<BusinessLogicException> {
                addOrderUseCase.add(orderForm).getOrThrow()
            }
        assertThat(exception.message).isEqualTo("Não é possível adicionar um pedido para um comando fechado")
    }

    @Test
    @Order(21)
    @DisplayName("Etapa 21: Garçom deve ser capaz de adicionar um produto com notas vazias à comanda aberta")
    fun `etapa 21 - garcom deve ser capaz de adicionar um produto com notas vazias a comanda aberta`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        // Ensure the command is open
        changeStatusCommandUseCase.execute(createdCommand.id.publicId, CommandStatusEnum.OPEN).getOrThrow()
        createdCommand = searchCommandUseCase.getById(createdCommand.id.publicId).getOrThrow()

        val initialOrdersPage =
            searchOrderUseCase
                .getAll(
                    PageableDTO(pageSize = 100),
                    OrderFilterDTO(
                        commandPublicId = createdCommand.id.publicId,
                    ),
                ).getOrThrow()
        val initialOrderCount = initialOrdersPage.content.size

        val products = searchProductUseCase.getByCompany(PageableDTO(pageSize = 1), createdCompany.publicId).getOrThrow().content
        assertThat(products).isNotEmpty()
        val productToAdd = products.first()

        val orderItemsForms =
            listOf(
                OrderItemsCreateForm(
                    productId = productToAdd.id.publicId,
                    notes = "", // Empty notes
                ),
            )

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )

        val addOrderResult = addOrderUseCase.add(orderForm)
        addOrderResult.onFailure { it.printStackTrace() }
        assertThat(addOrderResult.isSuccess).isTrue()

        val updatedOrdersPage =
            searchOrderUseCase
                .getAll(
                    PageableDTO(pageSize = 100),
                    OrderFilterDTO(
                        commandPublicId = createdCommand.id.publicId,
                    ),
                ).getOrThrow()
        assertThat(updatedOrdersPage.content.size).isGreaterThan(initialOrderCount)
        println("Produto com notas vazias adicionado à comanda '${createdCommand.name}'. Total de itens: ${updatedOrdersPage.content.size}")
    }

    @Test
    @Order(22)
    @DisplayName("Etapa 22: Garçom não deve ser capaz de adicionar um produto inexistente à comanda")
    fun `etapa 22 - garcom nao deve ser capaz de adicionar um produto inexistente a comanda`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        val nonExistentProductPublicId = UUID.randomUUID()

        val orderItemsForms =
            listOf(
                OrderItemsCreateForm(
                    productId = nonExistentProductPublicId,
                    notes = faker.lorem().sentence(5),
                ),
            )

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )

        val exception =
            assertThrows<NotFoundException> {
                addOrderUseCase.add(orderForm).getOrThrow()
            }
        assertThat(exception.message).isEqualTo("Produto não encontrado")
    }

    @Test
    @Order(23)
    @DisplayName("Etapa 23: Garçom não deve ser capaz de adicionar um produto de outra empresa à comanda")
    fun `etapa 23 - garcom nao deve ser capaz de adicionar um produto de outra empresa a comanda`() {
        whenever(currentUserService.getLoggedUser()).thenReturn(
            Result.success(
                UserAuthDTO(
                    sub = inviteeUser.sub,
                    name = inviteeUser.name,
                    email = inviteeUser.email,
                    emailVerified = true,
                    picture = null,
                ),
            ),
        )

        // Create a dummy company and product for another company
        val otherCompanyType = searchTypeCompanyUseCase.getByEnum(CompanyTypeEnum.RESTAURANT).getOrThrow()
        val otherCompanyForm =
            CompanyCreateRequest(
                name = faker.company().name(),
                email = faker.bothify("?????###@othercompany.com"),
                phone = faker.phoneNumber().cellPhone(),
                cnpj = faker.number().digits(14),
                description = faker.lorem().sentence(10),
                typeId = otherCompanyType.id.publicId,
            )
        val otherCompany: EntityId = createCompanyUseCase.create(otherCompanyForm).getOrThrow()

        val otherProductForm =
            ProductCreateRequest(
                name = faker.food().dish(),
                price = faker.number().randomDouble(2, 10, 200).toBigDecimal(),
                description = faker.lorem().sentence(10),
                ingredients = listOf(faker.food().ingredient()),
                servesPersons = 1,
                companyId = otherCompany.publicId,
                categoryId =
                    searchProductCategoryUseCase
                        .getByKey("appetizers")
                        .getOrThrow()
                        .id.publicId,
                availability = true,
            )
        val otherProduct = createProductUseCase.create(otherProductForm).getOrThrow()

        val orderItemsForms =
            listOf(
                OrderItemsCreateForm(
                    productId = otherProduct.publicId,
                    notes = faker.lorem().sentence(5),
                ),
            )

        val orderForm =
            OrderCreateRequest(
                commandId = createdCommand.id.publicId,
                items = orderItemsForms,
            )

        val exception =
            assertThrows<BusinessLogicException> {
                addOrderUseCase.add(orderForm).getOrThrow()
            }
        assertThat(exception.message).isNotNull
    }
}

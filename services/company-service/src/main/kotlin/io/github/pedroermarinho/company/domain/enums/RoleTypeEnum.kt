package io.github.pedroermarinho.company.domain.enums

enum class RoleTypeEnum(
    val value: String,
) {
    RESTAURANT_OWNER("restaurant_owner"),
    RESTAURANT_MANAGER("restaurant_manager"),
    WAITER("waiter"),
    KITCHEN_CHEF("kitchen_chef"),
    KITCHEN_STAFF("kitchen_staff"),
    BARTENDER("bartender"),
    CASHIER_RESTAURANT("cashier_restaurant"),
    HOST_HOSTESS("host_hostess"),
    CONSTRUCTION_OWNER("construction_owner"),
    CONSTRUCTION_MANAGER("construction_manager"),
    CIVIL_ENGINEER("civil_engineer"),
    MASTER_BUILDER("master_builder"),
    BRICKLAYER("bricklayer"),
    PAINTER_CONSTRUCTION("painter_construction"),
    WELDER_CONSTRUCTION("welder_construction"),
    ELECTRICIAN_CONSTRUCTION("electrician_construction"),
    PLUMBER_CONSTRUCTION("plumber_construction"),
    ADMIN_STAFF_CONSTRUCTION("admin_staff_construction"),
}

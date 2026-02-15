package io.github.pedroermarinho.comandalivreapi.company

import org.springframework.modulith.ApplicationModule
import org.springframework.modulith.NamedInterface
import org.springframework.modulith.PackageInfo

@PackageInfo
@NamedInterface("company")
@ApplicationModule(
    displayName = "Company Module",
    allowedDependencies = [
        "shared :: user_usecase",
        "shared :: user_dto",
        "shared :: user_form",
        "shared :: address_usecase",
        "shared :: address_dto",
        "shared :: address_form",
        "shared :: page_dto",
        "shared :: exception",
        "shared :: services",
        "shared :: util",
        "shared :: mapper",
        "shared :: event",
        "shared :: valueobject",
        "shared :: asset_usecase",
        "shared :: enums",
        "shared :: asset_dto",
        "shared :: annotations",
    ],
)
class ModuleMetadata

package io.github.pedroermarinho.comandalivreapi.prumodigital

import org.springframework.modulith.ApplicationModule
import org.springframework.modulith.NamedInterface
import org.springframework.modulith.PackageInfo

@PackageInfo
@NamedInterface("prumodigital")
@ApplicationModule(
    displayName = "Promo Digital Module",
    allowedDependencies = [
        "shared :: page_dto",
        "shared :: exception",
        "shared :: services",
        "shared :: enums",
        "shared :: user_usecase",
        "shared :: user_dto",
        "shared :: mapper",
        "shared :: util",
        "shared :: asset_dto",
        "shared :: asset_usecase",
        "shared :: valueobject",
        "company :: employee_usecase",
        "company :: employee_dto",
        "company :: company_usecase",
        "company :: company_dto",
        "company :: mapper",
        "company :: company_enums",
        "shared :: annotations",
        "shared :: address_usecase",
        "shared :: address_dto",
    ],
)
class ModuleMetadata

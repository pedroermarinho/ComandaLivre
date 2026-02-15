"""
Story: Full Restaurant Onboarding and Operation (Aligned with ITest)

This story simulates a complete end-to-end flow for a new restaurant.
It follows the same endpoint logic used in RestaurantOnboardingStoryITest.kt,
covering onboarding, command lifecycle, orders, products, and tables.
"""
import logging
import re
import uuid
from typing import List, Dict, Any, Optional

from rich.console import Console
from rich.panel import Panel
from rich.table import Table

from data_test.core.api_client import api_client
from data_test.core.data_generator import data_generator

console = Console()


class TestResult:
    """A simple class to hold the result of a single test step."""

    def __init__(self, description: str, status: str, details: str = ""):
        self.description = description
        self.status = status
        self.details = details


class RestaurantOnboardingStory:
    """Manages the state and execution of the restaurant onboarding story."""

    def __init__(self):
        self.results: List[TestResult] = []
        self.state: Dict[str, Any] = {
            "owner_token": None,
            "waiter_token": None,
            "company_id": None,
            "product_category_id": None,
            "product_ids": [],
            "table_ids": [],
            "invite_id": None,
            "invite_already_employee": False,
            "waiter_employee_id": None,
            "command_id": None,
            "current_table_id": None,
            "new_table_id": None,
            "owner_credentials": None,
            "waiter_credentials": None,
        }

    def _add_result(self, description: str, success: bool, details: str = ""):
        """Adds a test result to the list and logs it."""
        if success:
            status = "[bold green]Success[/]"
            logging.info(f"SUCCESS: {description} - {details}")
            console.print(f"  - {description}: {status}")
        else:
            status = "[bold red]Failure[/]"
            logging.error(f"FAILURE: {description} - {details}")
            console.print(f"  - {description}: {status}")
        self.results.append(TestResult(description, status, details))

    def _extract_id_from_response(self, response, fallback_keys: Optional[List[str]] = None) -> Optional[str]:
        """
        Extracts ID from Location header or falls back to JSON body.
        Assumes UUID is the last segment in the Location header path.
        """
        if fallback_keys is None:
            fallback_keys = ["publicId", "id"]

        if response and response.headers.get("Location"):
            location_header = response.headers["Location"]
            match = re.search(r"([0-9a-fA-F-]{36})$", location_header)
            if match:
                return match.group(1)

        if response is None:
            return None

        try:
            payload = response.json()
        except ValueError:
            return None

        for key in fallback_keys:
            value = payload.get(key)
            if value:
                return value
        return None

    def _get_json(self, response) -> Optional[Any]:
        if response is None:
            return None
        try:
            return response.json()
        except ValueError:
            return None

    def _get_command_status_key(self) -> Optional[str]:
        response = api_client.get_command_by_id(self.state["command_id"])
        if response and response.status_code == 200:
            payload = self._get_json(response) or {}
            return (payload.get("status") or {}).get("key")
        return None

    def _split_name(self, full_name: str) -> Dict[str, str]:
        parts = full_name.strip().split()
        if not parts:
            return {"first_name": "Usuario", "last_name": "Teste"}
        if len(parts) == 1:
            return {"first_name": parts[0], "last_name": "Teste"}
        return {"first_name": parts[0], "last_name": " ".join(parts[1:])}

    def _refresh_owner_token(self) -> bool:
        credentials = self.state.get("owner_credentials")
        if not credentials:
            return False
        response = api_client.authenticate_with_keycloak(
            username=credentials["username"],
            password=credentials["password"],
        )
        if response and response.status_code == 200 and "access_token" in response.json():
            token = response.json()["access_token"]
            self.state["owner_token"] = token
            api_client.set_token(token)
            return True
        return False

    def _refresh_waiter_token(self) -> bool:
        credentials = self.state.get("waiter_credentials")
        if not credentials:
            return False
        response = api_client.authenticate_with_keycloak(
            username=credentials["username"],
            password=credentials["password"],
        )
        if response and response.status_code == 200 and "access_token" in response.json():
            token = response.json()["access_token"]
            self.state["waiter_token"] = token
            api_client.set_token(token)
            return True
        return False

    def _ensure_keycloak_user(self, credential_key: str) -> Optional[Dict[str, str]]:
        credentials = self.state.get(credential_key)
        if credentials:
            return credentials

        user_data = data_generator.generate_user_data()
        name_parts = self._split_name(user_data["name"])
        user_id = api_client.create_keycloak_user(
            username=user_data["email"],
            email=user_data["email"],
            first_name=name_parts["first_name"],
            last_name=name_parts["last_name"],
            password=user_data["password"],
        )
        if not user_id:
            return None

        credentials = {
            "username": user_data["email"],
            "password": user_data["password"],
            "email": user_data["email"],
            "name": user_data["name"],
        }
        self.state[credential_key] = credentials
        return credentials

    def _fetch_invite_id_by_company(self, company_id: str, email: str) -> Optional[str]:
        response = api_client.get_employee_invites_by_company(company_id, params={"pageNumber": 0, "pageSize": 10})
        payload = self._get_json(response) or {}
        invites = payload.get("content") or []
        email_lower = email.lower()
        for invite in invites:
            user = invite.get("user") or {}
            invite_email = user.get("email") or ""
            if invite_email.lower() == email_lower:
                return invite.get("id")
        return None

    def run(self):
        """Executes all steps of the story in sequence."""
        console.print(Panel("[bold cyan]Executing Story: Full Restaurant Onboarding (Aligned)[/bold cyan]", expand=False))

        steps = [
            self._step_1_owner_authentication,
            self._step_2_owner_profile,
            self._step_3_create_company,
            self._step_4_seed_products_and_tables,
            self._step_5_invite_waiter,
            self._step_6_waiter_accepts_invite,
            self._step_7_waiter_creates_command,
            self._step_8_waiter_adds_order,
            self._step_9_owner_sets_command_paying,
            self._step_10_owner_closes_command,
            self._step_11_owner_reopens_command,
            self._step_12_waiter_changes_table,
            self._step_13_waiter_cannot_change_to_same_table_twice,
            self._step_14_waiter_cannot_change_to_other_company_table,
            self._step_15_waiter_cannot_change_table_when_closed,
            self._step_16_owner_cannot_reopen_open_command,
            self._step_17_waiter_adds_more_products,
            self._step_18_waiter_cannot_add_products_to_closed_command,
            self._step_19_waiter_adds_product_with_empty_notes,
            self._step_20_waiter_cannot_add_nonexistent_product,
            self._step_21_waiter_cannot_add_product_from_other_company,
            self._step_22_get_commands_count_unauthorized,
            self._step_23_get_bill_data,
            self._step_24_get_order_by_id,
            self._step_25_update_order_status,
            self._step_26_is_command_fully_closed,
            self._step_27_remove_order,
            self._step_28_update_product,
            self._step_29_update_product_availability,
            self._step_30_delete_product,
            self._step_31_get_table_list,
            self._step_32_get_table_by_id,
            self._step_33_create_tables_bulk,
            self._step_34_update_table,
            self._step_35_delete_table,
            self._step_36_flow_delete_table_orders_get_all,
        ]

        for step in steps:
            if not step():
                break

        self._print_summary()

    def _step_1_owner_authentication(self) -> bool:
        """Step 1: Authenticate owner with Keycloak."""
        description = "Authenticate owner with Keycloak"
        owner_credentials = self._ensure_keycloak_user("owner_credentials")
        if not owner_credentials:
            self._add_result(description, False, "Failed to create owner in Keycloak.")
            return False

        response = api_client.authenticate_with_keycloak(
            username=owner_credentials["username"],
            password=owner_credentials["password"],
        )
        if response and response.status_code == 200 and "access_token" in response.json():
            token = response.json()["access_token"]
            self.state["owner_token"] = token
            api_client.set_token(token)
            self._add_result(description, True, "Token received.")
            return True
        details = f"Status {response.status_code if response else 'N/A'}"
        self._add_result(description, False, details)
        return False

    def _step_2_owner_profile(self) -> bool:
        """Step 2: Create or load owner profile in the API."""
        description = "Create owner profile"
        response = api_client.auth()
        if response and response.status_code in (200, 201):
            self._add_result(description, True, "Owner profile loaded.")
            return True
        details = f"Status {response.status_code if response else 'N/A'}"
        self._add_result(description, False, details)
        return False

    def _step_3_create_company(self) -> bool:
        """Step 3: Owner creates a restaurant."""
        description = "Create restaurant company"
        company_data = data_generator.generate_company_data()

        response_create = api_client.create_company(company_data)
        if response_create and response_create.status_code == 201:
            self.state["company_id"] = self._extract_id_from_response(response_create)
            if self.state["company_id"]:
                self._add_result(description, True, f"Company '{company_data['name']}' created.")
                return True

        details = f"Status {response_create.status_code if response_create else 'N/A'}"
        self._add_result(description, False, f"Failed to create company. {details}")
        return False

    def _step_4_seed_products_and_tables(self) -> bool:
        """Step 4: Owner adds products and tables."""
        description = "Seed products and tables"
        response_cat = api_client.get_product_categories()
        if not (response_cat and response_cat.status_code == 200 and response_cat.json()):
            self._add_result(description, False, "Could not fetch product categories.")
            return False

        categories = response_cat.json()
        self.state["product_category_id"] = categories[0]["id"]

        for _ in range(3):
            product_data = data_generator.generate_product_data(
                self.state["company_id"],
                self.state["product_category_id"],
            )
            response_product = api_client.create_product(product_data)
            if not (response_product and response_product.status_code == 201):
                self._add_result(description, False, "Failed to create a product.")
                return False
            product_id = self._extract_id_from_response(response_product)
            if product_id:
                self.state["product_ids"].append(product_id)

        for idx in range(3):
            table_name = f"Mesa {idx + 1}"
            table_data = data_generator.generate_table_data(self.state["company_id"], table_name)
            response_table = api_client.create_table(table_data)
            if not (response_table and response_table.status_code == 201):
                self._add_result(description, False, "Failed to create a table.")
                return False
            table_id = self._extract_id_from_response(response_table)
            if table_id:
                self.state["table_ids"].append(table_id)

        if not self.state["product_ids"] or not self.state["table_ids"]:
            self._add_result(description, False, "Failed to capture created IDs.")
            return False

        self.state["current_table_id"] = self.state["table_ids"][0]
        self._add_result(description, True, "Seeded products and tables.")
        return True

    def _step_5_invite_waiter(self) -> bool:
        """Step 5: Owner invites the pre-configured test user as a waiter."""
        description = "Invite waiter employee"
        api_client.set_token(self.state["owner_token"])

        waiter_credentials = self._ensure_keycloak_user("waiter_credentials")
        if not waiter_credentials:
            self._add_result(description, False, "Failed to create waiter in Keycloak.")
            return False
        waiter_auth_response = api_client.authenticate_with_keycloak(
            username=waiter_credentials["username"],
            password=waiter_credentials["password"],
        )
        if not (waiter_auth_response and waiter_auth_response.status_code == 200):
            self._add_result(description, False, "Failed to authenticate waiter before invite.")
            return False
        api_client.set_token(waiter_auth_response.json().get("access_token"))
        waiter_profile_response = api_client.auth()
        if not (waiter_profile_response and waiter_profile_response.status_code in (200, 201)):
            self._add_result(description, False, "Failed to create waiter profile before invite.")
            return False

        api_client.set_token(self.state["owner_token"])

        response_roles = api_client.get_role_types_list()
        if not (response_roles and response_roles.status_code == 200):
            self._add_result(description, False, "Could not fetch role types.")
            return False

        roles = self._get_json(response_roles) or []
        waiter_role = next((role for role in roles if role.get("key") == "waiter"), None)
        if not waiter_role:
            self._add_result(description, False, "Could not find role 'waiter'.")
            return False
        role_id = waiter_role["id"]

        invite_data = data_generator.generate_employee_invite_data(
            self.state["company_id"],
            role_id,
            waiter_credentials["email"],
        )
        response_invite = api_client.invite_employee(invite_data)
        if response_invite and response_invite.status_code == 201:
            self.state["invite_id"] = self._extract_id_from_response(response_invite)
            if not self.state["invite_id"]:
                self.state["invite_id"] = self._fetch_invite_id_by_company(
                    self.state["company_id"],
                    waiter_credentials["email"],
                )
            if self.state["invite_id"]:
                self._add_result(description, True, f"Invite sent to {waiter_credentials['email']}.")
                return True
            self._add_result(description, False, "Invite created but ID not found.")
            return False
        if response_invite is not None:
            payload = self._get_json(response_invite) or {}
            if response_invite.status_code == 400 and payload.get("message") == "Usuário já é funcionário do restaurante":
                self.state["invite_already_employee"] = True
                self._add_result(description, True, "User already employee; invite skipped.")
                return True
            status_code = response_invite.status_code
            details = payload.get("message") or response_invite.text
            self._add_result(description, False, f"Failed to send invite. Status: {status_code}. {details}")
            return False

        self._add_result(description, False, "Failed to send invite. No response (request error).")
        return False

    def _step_6_waiter_accepts_invite(self) -> bool:
        """Step 6: Waiter authenticates and accepts the invite."""
        description = "Waiter accepts invite"
        waiter_credentials = self.state.get("waiter_credentials")
        if not waiter_credentials:
            self._add_result(description, False, "Missing waiter credentials.")
            return False

        waiter_auth_response = api_client.authenticate_with_keycloak(
            username=waiter_credentials["username"],
            password=waiter_credentials["password"],
        )
        if not (waiter_auth_response and waiter_auth_response.status_code == 200):
            self._add_result(description, False, "Waiter failed to authenticate with Keycloak.")
            return False

        self.state["waiter_token"] = waiter_auth_response.json()["access_token"]
        api_client.set_token(self.state["waiter_token"])

        profile_response = api_client.auth()
        if not (profile_response and profile_response.status_code in (200, 201)):
            self._add_result(description, False, "Waiter profile load failed.")
            return False

        if self.state["invite_already_employee"]:
            self._add_result(description, True, "Invite acceptance skipped (already employee).")
            return True

        if not self.state["invite_id"]:
            response_invites = api_client.get_my_employee_invites(params={"pageNumber": 0, "pageSize": 10})
            payload = self._get_json(response_invites) or {}
            invites = payload.get("content") or []
            for invite in invites:
                company = invite.get("company") or {}
                if company.get("id") == self.state["company_id"]:
                    self.state["invite_id"] = invite.get("id")
                    break
            if not self.state["invite_id"]:
                self._add_result(description, False, "Invite not found for waiter.")
                return False

        response_accept = api_client.accept_employee_invite(self.state["invite_id"])
        if response_accept and response_accept.status_code == 200:
            self._add_result(description, True, "Invite accepted successfully.")
            return True

        status_code = response_accept.status_code if response_accept else "N/A"
        self._add_result(description, False, f"Failed to accept invite. Status: {status_code}")
        return False

    def _step_7_waiter_creates_command(self) -> bool:
        """Step 7: Waiter creates a new command."""
        description = "Waiter creates command"
        api_client.set_token(self.state["waiter_token"])

        employees_response = api_client.get_employees_for_company(self.state["company_id"])
        tables_response = api_client.get_tables(
            params={"companyId": self.state["company_id"], "pageSize": 1}
        )

        employees_data = self._get_json(employees_response) or {}
        employee_id = employees_data.get("id")
        if not employee_id:
            content = employees_data.get("content") or []
            waiter_email = (self.state.get("waiter_credentials") or {}).get("email")
            employee_id = next(
                (item.get("id") for item in content if item.get("user", {}).get("email") == waiter_email),
                None,
            )

        tables_data = self._get_json(tables_response) or {}
        table_content = tables_data.get("content") or []

        if not (employee_id and table_content):
            self._add_result(description, False, "Could not fetch employees or tables for the company.")
            return False

        table_id = table_content[0]["id"]
        self.state["current_table_id"] = table_id
        self.state["waiter_employee_id"] = employee_id

        command_data = data_generator.generate_command_data(table_id, employee_id)
        command_response = api_client.create_command(command_data)

        if command_response and command_response.status_code == 201:
            self.state["command_id"] = self._extract_id_from_response(command_response)
            self._add_result(description, True, "Command created successfully.")
            return True

        status_code = command_response.status_code if command_response else "N/A"
        self._add_result(description, False, f"Failed to create command. Status: {status_code}")
        return False

    def _step_8_waiter_adds_order(self) -> bool:
        """Step 8: Waiter adds products to the command."""
        description = "Waiter adds order to command"
        api_client.set_token(self.state["waiter_token"])

        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 2},
        )
        products_data = self._get_json(products_response) or {}
        products = products_data.get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products to add to order.")
            return False

        product_ids = [product["id"] for product in products]
        order_data = data_generator.generate_order_form(self.state["command_id"], product_ids)
        order_response = api_client.add_order_to_command(order_data)

        if order_response and order_response.status_code == 201:
            self._add_result(description, True, f"{len(product_ids)} items added to command.")
            return True

        status_code = order_response.status_code if order_response else "N/A"
        self._add_result(description, False, f"Failed to add order. Status: {status_code}")
        return False

    def _step_9_owner_sets_command_paying(self) -> bool:
        """Step 9: Owner moves command to PAYING and validates status."""
        description = "Owner sets command to PAYING"
        if not self._refresh_owner_token():
            self._add_result(description, False, "Failed to refresh owner token.")
            return False

        response = api_client.update_command_status(self.state["command_id"], {"status": "PAYING"})
        if not (response and response.status_code == 200):
            status_code = response.status_code if response else "N/A"
            self._add_result(description, False, f"Failed to set PAYING. Status: {status_code}")
            return False

        status_key = self._get_command_status_key()
        if status_key == "paying":
            self._add_result(description, True, "Command status is paying.")
            return True

        self._add_result(description, False, f"Unexpected status: {status_key}")
        return False

    def _step_10_owner_closes_command(self) -> bool:
        """Step 10: Owner closes command and validates status."""
        description = "Owner closes command"
        api_client.set_token(self.state["owner_token"])

        response = api_client.update_command_status(
            self.state["command_id"],
            {"status": "CLOSED", "closeAll": True},
        )
        if not (response and response.status_code == 200):
            status_code = response.status_code if response else "N/A"
            self._add_result(description, False, f"Failed to close command. Status: {status_code}")
            return False

        status_key = self._get_command_status_key()
        if status_key == "closed":
            self._add_result(description, True, "Command status is closed.")
            return True

        self._add_result(description, False, f"Unexpected status: {status_key}")
        return False

    def _step_11_owner_reopens_command(self) -> bool:
        """Step 11: Owner reopens command."""
        description = "Owner reopens command"
        api_client.set_token(self.state["owner_token"])

        response = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
        if not (response and response.status_code == 200):
            status_code = response.status_code if response else "N/A"
            self._add_result(description, False, f"Failed to reopen command. Status: {status_code}")
            return False

        status_key = self._get_command_status_key()
        if status_key == "open":
            self._add_result(description, True, "Command status is open.")
            return True

        self._add_result(description, False, f"Unexpected status: {status_key}")
        return False

    def _step_12_waiter_changes_table(self) -> bool:
        """Step 12: Waiter changes the command table."""
        description = "Waiter changes command table"
        api_client.set_token(self.state["waiter_token"])

        new_table_data = data_generator.generate_table_data(self.state["company_id"], "Mesa Nova 1")
        response_table = api_client.create_table(new_table_data)
        if not (response_table and response_table.status_code == 201):
            self._add_result(description, False, "Failed to create new table.")
            return False

        new_table_id = self._extract_id_from_response(response_table)
        if not new_table_id:
            self._add_result(description, False, "Failed to extract new table ID.")
            return False

        self.state["new_table_id"] = new_table_id

        response_change = api_client.change_command_table(
            self.state["command_id"],
            {"newTableId": new_table_id},
        )
        if not (response_change and response_change.status_code == 200):
            status_code = response_change.status_code if response_change else "N/A"
            self._add_result(description, False, f"Failed to change table. Status: {status_code}")
            return False

        response_command = api_client.get_command_by_id(self.state["command_id"])
        payload = self._get_json(response_command) or {}
        table_id = (payload.get("table") or {}).get("id")

        if table_id == new_table_id:
            self._add_result(description, True, "Table changed successfully.")
            return True

        self._add_result(description, False, "Command table did not update.")
        return False

    def _step_13_waiter_cannot_change_to_same_table_twice(self) -> bool:
        """Step 13: Waiter cannot change command to the same table twice."""
        description = "Waiter cannot change to the same table twice"
        if not self._refresh_waiter_token():
            self._add_result(description, False, "Failed to refresh waiter token.")
            return False

        current_status = self._get_command_status_key()
        if current_status != "open":
            if not self._refresh_owner_token():
                self._add_result(description, False, "Failed to refresh owner token.")
                return False
            response_open = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
            if not (response_open and response_open.status_code == 200):
                status_code = response_open.status_code if response_open else "N/A"
                self._add_result(description, False, f"Failed to reopen command. Status: {status_code}")
                return False
            self._refresh_waiter_token()

        response_first = api_client.change_command_table(
            self.state["command_id"],
            {"newTableId": self.state["current_table_id"]},
        )
        if response_first is None:
            self._add_result(description, False, "Failed to change back to original table. No response.")
            return False
        if response_first.status_code != 200:
            details = (self._get_json(response_first) or {}).get("message") or response_first.text
            self._add_result(
                description,
                False,
                f"Failed to change back to original table. Status: {response_first.status_code}. {details}",
            )
            return False

        response_second = api_client.change_command_table(
            self.state["command_id"],
            {"newTableId": self.state["current_table_id"]},
        )
        if response_second is not None and response_second.status_code == 400:
            payload = self._get_json(response_second) or {}
            if payload.get("message") == "A comanda já está na mesa de destino.":
                self._add_result(description, True, "Rejected duplicate table change.")
                return True

        status_code = response_second.status_code if response_second is not None else "N/A"
        details = (self._get_json(response_second) or {}).get("message") if response_second is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_14_waiter_cannot_change_to_other_company_table(self) -> bool:
        """Step 14: Waiter cannot change command to another company's table."""
        description = "Waiter cannot change to other company table"
        api_client.set_token(self.state["waiter_token"])

        other_company = data_generator.generate_company_data()
        response_company = api_client.create_company(other_company)
        if not (response_company and response_company.status_code == 201):
            self._add_result(description, False, "Failed to create other company.")
            return False

        other_company_id = self._extract_id_from_response(response_company)
        if not other_company_id:
            self._add_result(description, False, "Failed to extract other company ID.")
            return False

        other_table_data = data_generator.generate_table_data(other_company_id, "Mesa Outra Empresa")
        response_table = api_client.create_table(other_table_data)
        if not (response_table and response_table.status_code == 201):
            self._add_result(description, False, "Failed to create table for other company.")
            return False

        other_table_id = self._extract_id_from_response(response_table)
        if not other_table_id:
            self._add_result(description, False, "Failed to extract other table ID.")
            return False

        response_change = api_client.change_command_table(
            self.state["command_id"],
            {"newTableId": other_table_id},
        )
        if response_change is not None and response_change.status_code == 400:
            payload = self._get_json(response_change) or {}
            if payload.get("message") == "A comanda e a mesa de destino devem pertencer à mesma empresa.":
                self._add_result(description, True, "Rejected cross-company table change.")
                return True

        status_code = response_change.status_code if response_change is not None else "N/A"
        details = (self._get_json(response_change) or {}).get("message") if response_change is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_15_waiter_cannot_change_table_when_closed(self) -> bool:
        """Step 15: Waiter cannot change table if command is not open."""
        description = "Waiter cannot change table when command closed"
        api_client.set_token(self.state["owner_token"])

        response_paying = api_client.update_command_status(self.state["command_id"], {"status": "PAYING"})
        response_closed = api_client.update_command_status(self.state["command_id"], {"status": "CLOSED"})

        if not (response_paying and response_paying.status_code == 200 and response_closed and response_closed.status_code == 200):
            self._add_result(description, False, "Failed to close command before change table attempt.")
            return False

        api_client.set_token(self.state["waiter_token"])
        tables_response = api_client.get_tables(
            params={"companyId": self.state["company_id"], "pageSize": 5}
        )
        tables_data = self._get_json(tables_response) or {}
        tables = tables_data.get("content") or []

        new_table_id = None
        for table in tables:
            if table.get("id") != self.state["current_table_id"]:
                new_table_id = table.get("id")
                break

        if not new_table_id:
            extra_table = data_generator.generate_table_data(self.state["company_id"], "Mesa Extra")
            response_table = api_client.create_table(extra_table)
            if not (response_table and response_table.status_code == 201):
                self._add_result(description, False, "Failed to create extra table.")
                return False
            new_table_id = self._extract_id_from_response(response_table)

        response_change = api_client.change_command_table(
            self.state["command_id"],
            {"newTableId": new_table_id},
        )
        if response_change is not None and response_change.status_code == 400:
            payload = self._get_json(response_change) or {}
            if payload.get("message") == "A comanda deve estar aberta para ter sua mesa alterada.":
                self._add_result(description, True, "Rejected change when closed.")
                return True

        status_code = response_change.status_code if response_change is not None else "N/A"
        details = (self._get_json(response_change) or {}).get("message") if response_change is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_16_owner_cannot_reopen_open_command(self) -> bool:
        """Step 16: Owner cannot reopen an already open command."""
        description = "Owner cannot reopen open command"
        api_client.set_token(self.state["owner_token"])

        response_open = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
        if not (response_open and response_open.status_code == 200):
            self._add_result(description, False, "Failed to open command before validation.")
            return False

        response_repeat = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
        if response_repeat is not None and response_repeat.status_code == 400:
            payload = self._get_json(response_repeat) or {}
            if payload.get("message") == "Transição de status de 'open' para 'open' não é permitida.":
                self._add_result(description, True, "Rejected duplicate open status.")
                return True

        status_code = response_repeat.status_code if response_repeat is not None else "N/A"
        details = (self._get_json(response_repeat) or {}).get("message") if response_repeat is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_17_waiter_adds_more_products(self) -> bool:
        """Step 17: Waiter adds more products to open command."""
        description = "Waiter adds more products"
        api_client.set_token(self.state["waiter_token"])

        initial_orders_response = api_client.get_orders({"commandId": self.state["command_id"]})
        initial_orders = (self._get_json(initial_orders_response) or {}).get("content") or []
        initial_count = len(initial_orders)

        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 2},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product_ids = [product["id"] for product in products[:2]]
        order_data = data_generator.generate_order_form(self.state["command_id"], product_ids)
        response_order = api_client.add_order_to_command(order_data)
        if not (response_order and response_order.status_code == 201):
            status_code = response_order.status_code if response_order else "N/A"
            self._add_result(description, False, f"Failed to add order. Status: {status_code}")
            return False

        updated_orders_response = api_client.get_orders({"commandId": self.state["command_id"]})
        updated_orders = (self._get_json(updated_orders_response) or {}).get("content") or []
        if len(updated_orders) > initial_count:
            self._add_result(description, True, "Order count increased.")
            return True

        self._add_result(description, False, "Order count did not increase.")
        return False

    def _step_18_waiter_cannot_add_products_to_closed_command(self) -> bool:
        """Step 18: Waiter cannot add products to a closed command."""
        description = "Waiter cannot add products to closed command"
        api_client.set_token(self.state["owner_token"])

        response_paying = api_client.update_command_status(self.state["command_id"], {"status": "PAYING"})
        response_closed = api_client.update_command_status(
            self.state["command_id"],
            {"status": "CLOSED", "closeAll": True},
        )
        if not (response_paying and response_paying.status_code == 200 and response_closed and response_closed.status_code == 200):
            self._add_result(description, False, "Failed to close command.")
            return False

        api_client.set_token(self.state["waiter_token"])
        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 1},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product_id = products[0]["id"]
        order_data = data_generator.generate_order_form(self.state["command_id"], [product_id])
        response_order = api_client.add_order_to_command(order_data)
        if response_order is not None and response_order.status_code == 400:
            payload = self._get_json(response_order) or {}
            if payload.get("message") == "Não é possível adicionar um pedido para um comando fechado":
                self._add_result(description, True, "Rejected order on closed command.")
                return True

        status_code = response_order.status_code if response_order is not None else "N/A"
        details = (self._get_json(response_order) or {}).get("message") if response_order is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_19_waiter_adds_product_with_empty_notes(self) -> bool:
        """Step 19: Waiter adds product with empty notes to open command."""
        description = "Waiter adds product with empty notes"
        api_client.set_token(self.state["owner_token"])

        response_open = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
        if not (response_open and response_open.status_code == 200):
            self._add_result(description, False, "Failed to reopen command.")
            return False

        api_client.set_token(self.state["waiter_token"])
        initial_orders_response = api_client.get_orders({"commandId": self.state["command_id"]})
        initial_orders = (self._get_json(initial_orders_response) or {}).get("content") or []
        initial_count = len(initial_orders)

        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 1},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product_id = products[0]["id"]
        order_data = data_generator.generate_order_form(
            self.state["command_id"],
            [product_id],
            notes=[""],
        )
        response_order = api_client.add_order_to_command(order_data)
        if not (response_order and response_order.status_code == 201):
            status_code = response_order.status_code if response_order else "N/A"
            self._add_result(description, False, f"Failed to add order. Status: {status_code}")
            return False

        updated_orders_response = api_client.get_orders({"commandId": self.state["command_id"]})
        updated_orders = (self._get_json(updated_orders_response) or {}).get("content") or []
        if len(updated_orders) > initial_count:
            self._add_result(description, True, "Order added with empty notes.")
            return True

        self._add_result(description, False, "Order count did not increase.")
        return False

    def _step_20_waiter_cannot_add_nonexistent_product(self) -> bool:
        """Step 20: Waiter cannot add a nonexistent product."""
        description = "Waiter cannot add nonexistent product"
        api_client.set_token(self.state["waiter_token"])

        product_id = str(uuid.uuid4())
        notes = data_generator.generate_order_notes(1)
        order_data = data_generator.generate_order_form(
            self.state["command_id"],
            [product_id],
            notes=notes,
        )
        response_order = api_client.add_order_to_command(order_data)
        if response_order is not None and response_order.status_code == 404:
            payload = self._get_json(response_order) or {}
            if payload.get("message") == "Produto não encontrado":
                self._add_result(description, True, "Rejected nonexistent product.")
                return True

        status_code = response_order.status_code if response_order is not None else "N/A"
        details = (self._get_json(response_order) or {}).get("message") if response_order is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_21_waiter_cannot_add_product_from_other_company(self) -> bool:
        """Step 21: Waiter cannot add a product from another company."""
        description = "Waiter cannot add product from other company"
        api_client.set_token(self.state["waiter_token"])

        other_company = data_generator.generate_company_data()
        response_company = api_client.create_company(other_company)
        if not (response_company and response_company.status_code == 201):
            self._add_result(description, False, "Failed to create other company.")
            return False

        other_company_id = self._extract_id_from_response(response_company)
        if not other_company_id:
            self._add_result(description, False, "Failed to extract other company ID.")
            return False

        response_categories = api_client.get_product_categories()
        if not (response_categories and response_categories.status_code == 200):
            self._add_result(description, False, "Failed to fetch product categories.")
            return False

        categories = response_categories.json()
        category = next((item for item in categories if item.get("key") == "appetizers"), categories[0])
        other_product_data = data_generator.generate_product_data(other_company_id, category["id"])
        response_product = api_client.create_product(other_product_data)
        if not (response_product and response_product.status_code == 201):
            self._add_result(description, False, "Failed to create other company product.")
            return False

        other_product_id = self._extract_id_from_response(response_product)
        if not other_product_id:
            self._add_result(description, False, "Failed to extract other product ID.")
            return False

        order_data = data_generator.generate_order_form(self.state["command_id"], [other_product_id])
        response_order = api_client.add_order_to_command(order_data)
        if response_order is not None and response_order.status_code == 400:
            payload = self._get_json(response_order) or {}
            if payload.get("message"):
                self._add_result(description, True, "Rejected product from other company.")
                return True

        status_code = response_order.status_code if response_order is not None else "N/A"
        details = (self._get_json(response_order) or {}).get("message") if response_order is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_22_get_commands_count_unauthorized(self) -> bool:
        """Step 22: Getting commands count should return 401."""
        description = "Commands count unauthorized"
        api_client.set_token(self.state["owner_token"])

        response = api_client.get_commands_count()
        if response is not None and response.status_code == 401:
            self._add_result(description, True, "Received 401 as expected.")
            return True

        status_code = response.status_code if response is not None else "N/A"
        details = (self._get_json(response) or {}).get("message") if response is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_23_get_bill_data(self) -> bool:
        """Step 23: Owner gets bill data for the command."""
        description = "Get command bill data"
        api_client.set_token(self.state["owner_token"])

        response = api_client.get_command_bill_data(self.state["command_id"])
        payload = self._get_json(response) or {}
        if response and response.status_code == 200 and payload.get("command") and payload.get("company") and payload.get("items") is not None:
            self._add_result(description, True, "Bill data returned.")
            return True

        status_code = response.status_code if response else "N/A"
        self._add_result(description, False, f"Failed to fetch bill data. Status: {status_code}")
        return False

    def _step_24_get_order_by_id(self) -> bool:
        """Step 24: Waiter fetches an order by ID."""
        description = "Get order by ID"
        api_client.set_token(self.state["waiter_token"])

        response_orders = api_client.get_orders({"commandId": self.state["command_id"]})
        orders = (self._get_json(response_orders) or {}).get("content") or []
        if not orders:
            self._add_result(description, False, "No orders found for command.")
            return False

        order_id = orders[0]["id"]
        response_order = api_client.get_order_by_id(order_id)
        payload = self._get_json(response_order) or {}

        if response_order and response_order.status_code == 200 and payload.get("id") == order_id:
            self._add_result(description, True, "Order retrieved.")
            return True

        status_code = response_order.status_code if response_order else "N/A"
        self._add_result(description, False, f"Failed to fetch order. Status: {status_code}")
        return False

    def _step_25_update_order_status(self) -> bool:
        """Step 25: Waiter updates order status."""
        description = "Update order status"
        api_client.set_token(self.state["waiter_token"])

        response_orders = api_client.get_orders({"commandId": self.state["command_id"]})
        orders = (self._get_json(response_orders) or {}).get("content") or []
        if not orders:
            self._add_result(description, False, "No orders found for command.")
            return False

        order_id = orders[0]["id"]
        response_update = api_client.update_order_status(order_id, {"status": "in_preparation"})
        if not (response_update and response_update.status_code == 200):
            status_code = response_update.status_code if response_update else "N/A"
            self._add_result(description, False, f"Failed to update order status. Status: {status_code}")
            return False

        response_order = api_client.get_order_by_id(order_id)
        status_key = ((self._get_json(response_order) or {}).get("status") or {}).get("key")
        if status_key == "in_preparation":
            self._add_result(description, True, "Order status updated.")
            return True

        self._add_result(description, False, f"Unexpected status: {status_key}")
        return False

    def _step_26_is_command_fully_closed(self) -> bool:
        """Step 26: Owner checks if the command is fully closed."""
        description = "Check command fully closed"
        api_client.set_token(self.state["owner_token"])

        response_paying = api_client.update_command_status(self.state["command_id"], {"status": "PAYING"})
        response_closed = api_client.update_command_status(
            self.state["command_id"],
            {"status": "CLOSED", "closeAll": True},
        )
        if not (response_paying and response_paying.status_code == 200 and response_closed and response_closed.status_code == 200):
            self._add_result(description, False, "Failed to close command.")
            return False

        response_check = api_client.is_command_fully_closed(self.state["command_id"])
        if response_check and response_check.status_code == 200:
            text = response_check.text.strip().lower()
            if text == "true" or (self._get_json(response_check) is True):
                self._add_result(description, True, "Command fully closed.")
                return True

        status_code = response_check.status_code if response_check else "N/A"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}")
        return False

    def _step_27_remove_order(self) -> bool:
        """Step 27: Waiter removes an order."""
        description = "Remove order"
        api_client.set_token(self.state["owner_token"])

        response_open = api_client.update_command_status(self.state["command_id"], {"status": "OPEN"})
        if not (response_open and response_open.status_code == 200):
            self._add_result(description, False, "Failed to open command.")
            return False

        api_client.set_token(self.state["waiter_token"])
        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 1},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product_id = products[0]["id"]
        order_data = data_generator.generate_order_form(
            self.state["command_id"],
            [product_id],
            notes=["Pedido para remover"],
        )
        response_order = api_client.add_order_to_command(order_data)
        if not (response_order and response_order.status_code == 201):
            status_code = response_order.status_code if response_order else "N/A"
            self._add_result(description, False, f"Failed to add order. Status: {status_code}")
            return False

        orders_response = api_client.get_orders({"commandId": self.state["command_id"]})
        orders = (self._get_json(orders_response) or {}).get("content") or []
        order_id = next((item["id"] for item in orders if item.get("notes") == "Pedido para remover"), None)
        if not order_id:
            self._add_result(description, False, "Could not find order to remove.")
            return False

        response_delete = api_client.delete_order(order_id)
        if response_delete is None:
            self._add_result(description, False, "Failed to delete order. No response.")
            return False
        if response_delete.status_code != 204:
            details = (self._get_json(response_delete) or {}).get("message") or response_delete.text
            self._add_result(
                description,
                False,
                f"Failed to delete order. Status: {response_delete.status_code}. {details}",
            )
            return False

        response_get = api_client.get_order_by_id(order_id)
        if response_get is not None and response_get.status_code == 404:
            self._add_result(description, True, "Order removed and not found.")
            return True

        status_code = response_get.status_code if response_get is not None else "N/A"
        details = (self._get_json(response_get) or {}).get("message") if response_get is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_28_update_product(self) -> bool:
        """Step 28: Owner updates a product."""
        description = "Update product"
        api_client.set_token(self.state["owner_token"])

        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 1},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product = products[0]
        product_id = product["id"]
        category_id = (product.get("category") or {}).get("id") or self.state["product_category_id"]

        update_data = data_generator.generate_product_update_data(self.state["company_id"], category_id)
        response_update = api_client.update_product(product_id, update_data)
        if not (response_update and response_update.status_code == 200):
            status_code = response_update.status_code if response_update else "N/A"
            self._add_result(description, False, f"Failed to update product. Status: {status_code}")
            return False

        response_get = api_client.get_public_product_by_id(product_id)
        payload = self._get_json(response_get) or {}
        if response_get and response_get.status_code == 200 and payload.get("name") == update_data["name"]:
            self._add_result(description, True, "Product updated.")
            return True

        status_code = response_get.status_code if response_get else "N/A"
        self._add_result(description, False, f"Failed to verify product update. Status: {status_code}")
        return False

    def _step_29_update_product_availability(self) -> bool:
        """Step 29: Owner updates product availability."""
        description = "Update product availability"
        api_client.set_token(self.state["owner_token"])

        products_response = api_client.get_public_products(
            self.state["company_id"],
            params={"pageSize": 1},
        )
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products.")
            return False

        product = products[0]
        product_id = product["id"]
        current_availability = product.get("availability")
        new_availability = not current_availability

        response_update = api_client.update_product_status(product_id, new_availability)
        if not (response_update and response_update.status_code == 200):
            status_code = response_update.status_code if response_update else "N/A"
            self._add_result(description, False, f"Failed to update availability. Status: {status_code}")
            return False

        response_get = api_client.get_public_product_by_id(product_id)
        payload = self._get_json(response_get) or {}
        if response_get and response_get.status_code == 200 and payload.get("availability") == new_availability:
            self._add_result(description, True, "Availability updated.")
            return True

        status_code = response_get.status_code if response_get else "N/A"
        self._add_result(description, False, f"Failed to verify availability. Status: {status_code}")
        return False

    def _step_30_delete_product(self) -> bool:
        """Step 30: Owner deletes a product."""
        description = "Delete product"
        api_client.set_token(self.state["owner_token"])

        product_data = data_generator.generate_product_data(
            self.state["company_id"],
            self.state["product_category_id"],
        )
        response_create = api_client.create_product(product_data)
        if not (response_create and response_create.status_code == 201):
            self._add_result(description, False, "Failed to create product for deletion.")
            return False

        product_id = self._extract_id_from_response(response_create)
        if not product_id:
            self._add_result(description, False, "Failed to extract product ID.")
            return False

        response_delete = api_client.delete_product(product_id)
        if not (response_delete and response_delete.status_code == 204):
            status_code = response_delete.status_code if response_delete else "N/A"
            self._add_result(description, False, f"Failed to delete product. Status: {status_code}")
            return False

        response_get = api_client.get_public_product_by_id(product_id)
        if response_get is not None and response_get.status_code == 404:
            self._add_result(description, True, "Product deleted and not found.")
            return True

        status_code = response_get.status_code if response_get is not None else "N/A"
        details = (self._get_json(response_get) or {}).get("message") if response_get is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_31_get_table_list(self) -> bool:
        """Step 31: Owner fetches the list of tables."""
        description = "Get table list"
        api_client.set_token(self.state["owner_token"])

        response = api_client.get_tables_list({"companyId": self.state["company_id"]})
        payload = self._get_json(response) or []
        tables = payload if isinstance(payload, list) else payload.get("content") or []
        if response and response.status_code == 200 and len(tables) > 0:
            self._add_result(description, True, "Tables listed.")
            return True

        status_code = response.status_code if response else "N/A"
        self._add_result(description, False, f"Failed to list tables. Status: {status_code}")
        return False

    def _step_32_get_table_by_id(self) -> bool:
        """Step 32: Owner fetches a table by ID."""
        description = "Get table by ID"
        api_client.set_token(self.state["owner_token"])

        response_list = api_client.get_tables_list({"companyId": self.state["company_id"]})
        payload = self._get_json(response_list) or []
        tables = payload if isinstance(payload, list) else payload.get("content") or []
        if not tables:
            self._add_result(description, False, "No tables available.")
            return False

        table_id = tables[0]["id"]
        response_get = api_client.get_table_by_id(table_id)
        payload = self._get_json(response_get) or {}
        if response_get and response_get.status_code == 200 and payload.get("id") == table_id:
            self._add_result(description, True, "Table retrieved.")
            return True

        status_code = response_get.status_code if response_get else "N/A"
        self._add_result(description, False, f"Failed to fetch table. Status: {status_code}")
        return False

    def _step_33_create_tables_bulk(self) -> bool:
        """Step 33: Owner creates tables in bulk."""
        description = "Create tables in bulk"
        api_client.set_token(self.state["owner_token"])

        bulk_data = data_generator.generate_table_bulk_data(
            self.state["company_id"],
            start=10,
            end=99,
            num_people=4,
            description="Mesas criadas em lote",
        )
        response_bulk = api_client.create_tables_bulk(bulk_data)
        if not (response_bulk and response_bulk.status_code == 200):
            status_code = response_bulk.status_code if response_bulk else "N/A"
            self._add_result(description, False, f"Failed to create bulk tables. Status: {status_code}")
            return False

        response_tables = api_client.get_tables(
            params={"companyId": self.state["company_id"], "search": "Mesa 100"}
        )
        payload = self._get_json(response_tables) or {}
        content = payload.get("content") or []
        if response_tables and response_tables.status_code == 200 and len(content) >= 1:
            self._add_result(description, True, "Bulk tables created.")
            return True

        status_code = response_tables.status_code if response_tables else "N/A"
        self._add_result(description, False, f"Failed to verify bulk tables. Status: {status_code}")
        return False

    def _step_34_update_table(self) -> bool:
        """Step 34: Owner updates a table."""
        description = "Update table"
        api_client.set_token(self.state["owner_token"])

        response_tables = api_client.get_tables(
            params={"companyId": self.state["company_id"], "pageSize": 1}
        )
        payload = self._get_json(response_tables) or {}
        content = payload.get("content") or []
        if not content:
            self._add_result(description, False, "No tables to update.")
            return False

        table_id = content[0]["id"]
        update_data = data_generator.generate_table_update_data()
        response_update = api_client.update_table(table_id, update_data)
        if not (response_update and response_update.status_code == 200):
            status_code = response_update.status_code if response_update else "N/A"
            self._add_result(description, False, f"Failed to update table. Status: {status_code}")
            return False

        response_get = api_client.get_table_by_id(table_id)
        payload = self._get_json(response_get) or {}
        if response_get and response_get.status_code == 200 and payload.get("name") == update_data["name"]:
            self._add_result(description, True, "Table updated.")
            return True

        status_code = response_get.status_code if response_get else "N/A"
        self._add_result(description, False, f"Failed to verify table update. Status: {status_code}")
        return False

    def _step_35_delete_table(self) -> bool:
        """Step 35: Owner deletes a table."""
        description = "Delete table"
        api_client.set_token(self.state["owner_token"])

        table_data = data_generator.generate_table_data(self.state["company_id"], "Mesa para Deletar")
        response_create = api_client.create_table(table_data)
        if not (response_create and response_create.status_code == 201):
            self._add_result(description, False, "Failed to create table for deletion.")
            return False

        table_id = self._extract_id_from_response(response_create)
        if not table_id:
            self._add_result(description, False, "Failed to extract table ID.")
            return False

        response_delete = api_client.delete_table(table_id)
        if not (response_delete and response_delete.status_code == 204):
            status_code = response_delete.status_code if response_delete else "N/A"
            self._add_result(description, False, f"Failed to delete table. Status: {status_code}")
            return False

        response_get = api_client.get_table_by_id(table_id)
        if response_get is not None and response_get.status_code == 404:
            self._add_result(description, True, "Table deleted and not found.")
            return True

        status_code = response_get.status_code if response_get is not None else "N/A"
        details = (self._get_json(response_get) or {}).get("message") if response_get is not None else "No response"
        self._add_result(description, False, f"Unexpected response. Status: {status_code}. {details}")
        return False

    def _step_36_flow_delete_table_orders_get_all(self) -> bool:
        """Step 36: Create table, open command, add order, delete table, verify orders getAll."""
        description = "Flow delete table and verify orders getAll"
        api_client.set_token(self.state["owner_token"])

        table_data = data_generator.generate_table_data(self.state["company_id"], "Mesa Fluxo")
        response_table = api_client.create_table(table_data)
        if not (response_table and response_table.status_code == 201):
            self._add_result(description, False, "Failed to create flow table.")
            return False

        new_table_id = self._extract_id_from_response(response_table)
        if not new_table_id:
            self._add_result(description, False, "Failed to extract flow table ID.")
            return False

        if not self.state["waiter_employee_id"]:
            self._add_result(description, False, "Missing waiter employee ID.")
            return False

        api_client.set_token(self.state["waiter_token"])
        command_data = data_generator.generate_command_data(new_table_id, self.state["waiter_employee_id"])
        response_command = api_client.create_command(command_data)
        if not (response_command and response_command.status_code == 201):
            status_code = response_command.status_code if response_command else "N/A"
            self._add_result(description, False, f"Failed to create flow command. Status: {status_code}")
            return False

        new_command_id = self._extract_id_from_response(response_command)
        if not new_command_id:
            self._add_result(description, False, "Failed to extract flow command ID.")
            return False

        products_response = api_client.get_public_products(self.state["company_id"])
        products = (self._get_json(products_response) or {}).get("content") or []
        if not products:
            self._add_result(description, False, "Could not fetch products for flow.")
            return False

        product_id = products[0]["id"]
        order_data = data_generator.generate_order_form(new_command_id, [product_id], notes=["Pedido de fluxo"])
        response_order = api_client.add_order_to_command(order_data)
        if not (response_order and response_order.status_code == 201):
            status_code = response_order.status_code if response_order else "N/A"
            self._add_result(description, False, f"Failed to add flow order. Status: {status_code}")
            return False

        api_client.set_token(self.state["owner_token"])
        response_delete = api_client.delete_table(new_table_id)
        if not (response_delete and response_delete.status_code == 204):
            status_code = response_delete.status_code if response_delete else "N/A"
            self._add_result(description, False, f"Failed to delete flow table. Status: {status_code}")
            return False

        api_client.set_token(self.state["waiter_token"])
        response_orders = api_client.get_orders({"companyId": self.state["company_id"]})
        if response_orders and response_orders.status_code == 200:
            self._add_result(description, True, "Orders getAll returned 200.")
            return True

        status_code = response_orders.status_code if response_orders else "N/A"
        self._add_result(description, False, f"Orders getAll failed. Status: {status_code}")
        return False

    def _print_summary(self):
        """Prints the final results table."""
        table = Table(title="Restaurant Onboarding Story Summary")
        table.add_column("Step", justify="left", style="cyan", no_wrap=True)
        table.add_column("Result", justify="center")
        table.add_column("Details", justify="left", no_wrap=False)

        for result in self.results:
            table.add_row(result.description, result.status, result.details)

        console.print(table)


def run_story():
    """Entry point for the story, called by the CLI."""
    story = RestaurantOnboardingStory()
    story.run()

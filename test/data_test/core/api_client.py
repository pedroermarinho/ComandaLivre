"""
API Client for interacting with the ComandaLivre REST API.

This module provides a client class to handle HTTP requests, authentication,
and responses from the API, abstracting the details of the HTTP calls
from the main test logic.
"""
import logging
import re
import uuid
from typing import Dict, Any, Optional

import requests
from requests import Response, Session

from data_test.core.config import (
    API_BASE_URL,
    KEYCLOAK_BASE_URL,
    KEYCLOAK_TOKEN_ENDPOINT,
    KEYCLOAK_CLIENT_ID,
    KEYCLOAK_CLIENT_SECRET,
    KEYCLOAK_ADMIN_CLIENT_ID,
    KEYCLOAK_ADMIN_PASSWORD,
    KEYCLOAK_ADMIN_TOKEN_ENDPOINT,
    KEYCLOAK_ADMIN_USERNAME,
    TEST_USER_USERNAME,
    TEST_USER_PASSWORD
)


class ApiClient:
    """
    A client to interact with the application's REST API.
    """

    def __init__(self):
        """Initializes a requests session and the auth token."""
        self.session: Session = requests.Session()
        self.token: Optional[str] = None
        self.session.headers.update({"Content-Type": "application/json"})

    def _update_auth_header(self):
        """Updates the session headers with the Bearer token."""
        if self.token:
            self.session.headers.update({"Authorization": f"Bearer {self.token}"})
        else:
            self.session.headers.pop("Authorization", None)
            logging.warning("No auth token set. Making unauthenticated request.")

    def _make_request(self, method: str, endpoint: str, **kwargs) -> Optional[Response]:
        """
        A generic request maker to handle exceptions and logging.
        """
        try:
            # Ensure session headers are up-to-date for every request
            current_headers = self.session.headers.copy()
            if 'headers' in kwargs:
                current_headers.update(kwargs.pop('headers'))

            response = self.session.request(method, endpoint, timeout=15, headers=current_headers, **kwargs)
            return response
        except requests.exceptions.RequestException as e:
            logging.error(f"Request to {endpoint} failed: {e}")
            return None

    def set_token(self, token: str):
        """Manually sets the auth token and updates the session header."""
        self.token = token
        self._update_auth_header()

    def get(self, endpoint_path: str, params: Optional[Dict] = None) -> Optional[Response]:
        """Generic GET request helper."""
        return self._make_request("GET", f"{API_BASE_URL}/{endpoint_path}", params=params)

    def post(self, endpoint_path: str, data: Dict) -> Optional[Response]:
        """Generic POST request helper."""
        return self._make_request("POST", f"{API_BASE_URL}/{endpoint_path}", json=data)

    def patch(self, endpoint_path: str, data: Dict) -> Optional[Response]:
        """Generic PATCH request helper."""
        return self._make_request("PATCH", f"{API_BASE_URL}/{endpoint_path}", json=data)

    def put(self, endpoint_path: str, data: Dict) -> Optional[Response]:
        """Generic PUT request helper."""
        return self._make_request("PUT", f"{API_BASE_URL}/{endpoint_path}", json=data)

    def delete(self, endpoint_path: str) -> Optional[Response]:
        """Generic DELETE request helper."""
        return self._make_request("DELETE", f"{API_BASE_URL}/{endpoint_path}")

    def get_public_companies(self, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets public companies, optionally with query parameters."""
        return self.get("company/companies", params=params)

    def get_public_company_by_id(self, company_id: str) -> Optional[Response]:
        """Gets a single public company by its ID."""
        return self.get(f"company/companies/{company_id}")

    def get_public_products(self, company_id: str, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets public products for a specific company."""
        if params is None:
            params = {}
        params["companyId"] = company_id
        return self.get("comandalivre/products", params=params)

    def get_public_product_by_id(self, product_id: str) -> Optional[Response]:
        """Gets a single public product by its ID."""
        return self.get(f"comandalivre/products/{product_id}")

    def create_company(self, company_data: Dict[str, Any]) -> Optional[Response]:
        """Creates a new company."""
        return self.post("company/companies", data=company_data)

    def get_company_type_by_enum(self, enum_name: str) -> Optional[Response]:
        """Gets a company type by its enum name (e.g., RESTAURANT)."""
        return self.get(f"company/company-types/by-enum/{enum_name}")

    def get_product_categories(self) -> Optional[Response]:
        """Gets all product categories."""
        return self.get("comandalivre/product-categories/list")

    def create_product(self, product_data: Dict[str, Any]) -> Optional[Response]:
        """Creates a new product."""
        return self.post("comandalivre/products", data=product_data)

    def update_product(self, product_id: str, product_data: Dict[str, Any]) -> Optional[Response]:
        """Updates an existing product."""
        return self.put(f"comandalivre/products/{product_id}", data=product_data)

    def update_product_status(self, product_id: str, status: bool) -> Optional[Response]:
        """Updates the availability status of a product."""
        return self.patch(f"comandalivre/products/{product_id}/status/{status}", data={})

    def delete_product(self, product_id: str) -> Optional[Response]:
        """Deletes a product."""
        return self.delete(f"comandalivre/products/{product_id}")

    def create_table(self, table_data: Dict[str, Any]) -> Optional[Response]:
        """Creates a new table."""
        return self.post("comandalivre/tables", data=table_data)

    def create_tables_bulk(self, bulk_data: Dict[str, Any]) -> Optional[Response]:
        """Creates tables in bulk."""
        return self.post("comandalivre/tables/bulk", data=bulk_data)

    def get_tables(self, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets tables with pagination."""
        return self.get("comandalivre/tables", params=params)

    def get_tables_list(self, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets the full list of tables."""
        return self.get("comandalivre/tables/list", params=params)

    def get_table_by_id(self, table_id: str) -> Optional[Response]:
        """Gets a single table by ID."""
        return self.get(f"comandalivre/tables/{table_id}")

    def update_table(self, table_id: str, table_data: Dict[str, Any]) -> Optional[Response]:
        """Updates an existing table."""
        return self.put(f"comandalivre/tables/{table_id}", data=table_data)

    def delete_table(self, table_id: str) -> Optional[Response]:
        """Deletes a table."""
        return self.delete(f"comandalivre/tables/{table_id}")

    def get_role_type_by_enum(self, enum_name: str) -> Optional[Response]:
        """Gets a role type by its enum name (e.g., WAITER)."""
        return self.get(f"company/role-types/by-enum/{enum_name}")

    def get_role_types_list(self) -> Optional[Response]:
        """Gets the list of role types."""
        return self.get("company/role-types/list")

    def invite_employee(self, invite_data: Dict[str, Any]) -> Optional[Response]:
        """Invites a new employee to a company."""
        return self.post("company/employees/invites", data=invite_data)

    def accept_employee_invite(self, invite_id: str) -> Optional[Response]:
        """Accepts an employee invitation."""
        return self.patch(f"company/employees/invites/{invite_id}/accept", data={})

    def get_employees_for_company(self, company_id: str) -> Optional[Response]:
        """Gets all employees for a given company."""
        return self.get(f"company/employees/by-company/{company_id}")

    def get_employee_invites_by_company(self, company_id: str, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets employee invites for a company."""
        return self.get(f"company/employees/invites/company/{company_id}", params=params)

    def get_my_employee_invites(self, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets employee invites for the logged-in user."""
        return self.get("company/employees/invites/", params=params)

    def create_command(self, command_data: Dict[str, Any]) -> Optional[Response]:
        """Creates a new command (comanda)."""
        return self.post("comandalivre/commands", data=command_data)

    def get_command_by_id(self, command_id: str) -> Optional[Response]:
        """Gets a command by ID."""
        return self.get(f"comandalivre/commands/{command_id}")

    def update_command_status(self, command_id: str, status_data: Dict[str, Any]) -> Optional[Response]:
        """Updates the status of a command."""
        return self.patch(f"comandalivre/commands/{command_id}/status", data=status_data)

    def change_command_table(self, command_id: str, change_data: Dict[str, Any]) -> Optional[Response]:
        """Changes the table for a command."""
        return self.patch(f"comandalivre/commands/{command_id}/change-table", data=change_data)

    def get_commands_count(self) -> Optional[Response]:
        """Gets the count of commands."""
        return self.get("comandalivre/commands/count")

    def get_command_bill_data(self, command_id: str) -> Optional[Response]:
        """Gets bill data for a command."""
        return self.get(f"comandalivre/commands/{command_id}/bill-data")

    def add_order_to_command(self, order_data: Dict[str, Any]) -> Optional[Response]:
        """Adds items as an order to a command."""
        return self.post("comandalivre/orders", data=order_data)

    def get_orders(self, params: Optional[Dict] = None) -> Optional[Response]:
        """Gets orders with optional query parameters."""
        return self.get("comandalivre/orders", params=params)

    def get_order_by_id(self, order_id: str) -> Optional[Response]:
        """Gets a single order by ID."""
        return self.get(f"comandalivre/orders/{order_id}")

    def update_order_status(self, order_id: str, status_data: Dict[str, Any]) -> Optional[Response]:
        """Updates the status of an order."""
        return self.patch(f"comandalivre/orders/{order_id}/status", data=status_data)

    def delete_order(self, order_id: str) -> Optional[Response]:
        """Deletes an order."""
        return self.delete(f"comandalivre/orders/{order_id}")

    def is_command_fully_closed(self, command_id: str) -> Optional[Response]:
        """Checks if a command is fully closed."""
        return self.get("comandalivre/orders/is-command-fully-closed", params={"commandId": command_id})

    def auth(self)-> Optional[Response]:
        """
        Authenticates a user e retorna os dados do usuario
        """
        result = self.post("shared/users/auth",data={})
        return result


    def authenticate_with_keycloak(self, username: Optional[str] = None, password: Optional[str] = None) -> Optional[Response]:
        """
        Authenticates against Keycloak using the password grant type.
        This is for testing purposes, simulating a backend client.
        """
        username = username or TEST_USER_USERNAME
        password = password or TEST_USER_PASSWORD
        payload = {
            'grant_type': 'password',
            'client_id': KEYCLOAK_CLIENT_ID,
            'client_secret': KEYCLOAK_CLIENT_SECRET,
            'username': username,
            'password': password,
        }
        # For this specific request, the Content-Type must be form-urlencoded
        headers = {'Content-Type': 'application/x-www-form-urlencoded'}

        logging.info(f"Attempting Keycloak authentication for user '{username}'")
        # We use requests.post directly here because it's a one-off call
        # that uses a different Content-Type and doesn't rely on the session's bearer token.
        try:
            response = requests.post(KEYCLOAK_TOKEN_ENDPOINT, data=payload, headers=headers, timeout=15)
            return response
        except requests.exceptions.RequestException as e:
            logging.error(f"Keycloak authentication request failed: {e}")
            return None

    def authenticate_keycloak_admin(self) -> Optional[str]:
        """Authenticates as Keycloak admin and returns an access token."""
        payload = {
            'grant_type': 'password',
            'client_id': KEYCLOAK_ADMIN_CLIENT_ID,
            'username': KEYCLOAK_ADMIN_USERNAME,
            'password': KEYCLOAK_ADMIN_PASSWORD,
        }
        headers = {'Content-Type': 'application/x-www-form-urlencoded'}
        try:
            response = requests.post(KEYCLOAK_ADMIN_TOKEN_ENDPOINT, data=payload, headers=headers, timeout=15)
        except requests.exceptions.RequestException as e:
            logging.error(f"Keycloak admin authentication request failed: {e}")
            return None
        if response.status_code != 200:
            logging.error(f"Keycloak admin authentication failed: {response.status_code} {response.text}")
            return None
        try:
            return response.json().get("access_token")
        except ValueError:
            logging.error("Keycloak admin authentication response is not valid JSON.")
            return None

    def create_keycloak_user(
        self,
        username: str,
        email: str,
        first_name: str,
        last_name: str,
        password: str,
        realm: str = "comandalivre",
    ) -> Optional[str]:
        """Creates a Keycloak user and returns its ID."""
        admin_token = self.authenticate_keycloak_admin()
        if not admin_token:
            return None

        headers = {"Authorization": f"Bearer {admin_token}", "Content-Type": "application/json"}
        user_payload = {
            "username": username,
            "email": email,
            "enabled": True,
            "emailVerified": True,
            "firstName": first_name,
            "lastName": last_name,
            "credentials": [
                {"type": "password", "value": password, "temporary": False}
            ],
        }

        try:
            response = requests.post(
                f"{KEYCLOAK_BASE_URL}/admin/realms/{realm}/users",
                headers=headers,
                json=user_payload,
                timeout=15,
            )
        except requests.exceptions.RequestException as e:
            logging.error(f"Keycloak create user request failed: {e}")
            return None

        if response.status_code == 201:
            location = response.headers.get("Location", "")
            match = re.search(r"/([^/]+)$", location)
            return match.group(1) if match else None

        if response.status_code == 409:
            return self.find_keycloak_user_id(username, realm, admin_token)

        logging.error(f"Keycloak create user failed: {response.status_code} {response.text}")
        return None

    def find_keycloak_user_id(self, username: str, realm: str, admin_token: str) -> Optional[str]:
        """Finds a Keycloak user ID by username."""
        headers = {"Authorization": f"Bearer {admin_token}"}
        try:
            response = requests.get(
                f"{KEYCLOAK_BASE_URL}/admin/realms/{realm}/users",
                headers=headers,
                params={"username": username},
                timeout=15,
            )
        except requests.exceptions.RequestException as e:
            logging.error(f"Keycloak lookup user request failed: {e}")
            return None
        if response.status_code != 200:
            logging.error(f"Keycloak lookup user failed: {response.status_code} {response.text}")
            return None
        try:
            users = response.json()
        except ValueError:
            logging.error("Keycloak lookup user response is not valid JSON.")
            return None
        if not users:
            return None
        return users[0].get("id")




# --- Singleton Instance ---
api_client = ApiClient()

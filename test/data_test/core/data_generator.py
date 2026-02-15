import logging
import re
from typing import Dict, Any, List, Optional

from faker import Faker
from faker.providers import BaseProvider
# from faker_br.providers.cnpj import Provider as CnpjProvider # Removed this import

from data_test.core.config import FAKER_LOCALE

import random
import uuid # This import was missing from the original code and is needed for _generate_unique_email

# --- Custom Provider for Brazilian Phone Numbers ---

class PtBrPhoneProvider(BaseProvider):
    """
    A Faker provider for generating Brazilian phone numbers in the common
    format (XX) 9XXXX-XXXX.
    """
    def pt_br_cellphone(self) -> str:
        """Generates a random Brazilian mobile phone number."""
        area_code = self.random_element(
            elements=(
                "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "21", "22", "24", "27", "28", "31", "32", "33", "34",
                "35", "37", "38", "41", "42", "43", "44", "45", "46",
                "47", "48", "49", "51", "53", "54", "55", "61", "62",
                "63", "64", "65", "66", "67", "68", "69", "71", "73",
                "74", "75", "77", "79", "81", "82", "83", "84", "85",
                "86", "87", "88", "89", "91", "92", "93", "94", "95",
                "96", "97", "98", "99"
            )
        )
        first_part = f"{random.randint(7, 9)}{''.join([str(random.randint(0, 9)) for _ in range(3)])}"
        second_part = f"{''.join([str(random.randint(0, 9)) for _ in range(4)])}"
        return f"({area_code}) 9{first_part}-{second_part}"


# --- DataGenerator Class ---

class DataGenerator:
    """
    Handles the generation of all necessary synthetic data for the tests.
    """
    def __init__(self, locale: str = FAKER_LOCALE):
        """
        Initializes the Faker instance and adds the custom provider.
        """
        self.faker = Faker(locale)
        self.faker.add_provider(PtBrPhoneProvider)
        try:
            from faker.providers.food import Provider as FoodProvider
            self.faker.add_provider(FoodProvider)
        except ImportError:
            logging.warning("faker.providers.food not available; using fallback food data.")

    def _food_dish(self) -> str:
        try:
            name = self.faker.food.dish()
        except AttributeError:
            name = self.faker.word().title()
        if len(name) < 3:
            name = f"{name} {self.faker.word().title()}".strip()
        return name[:100]

    def _food_ingredient(self) -> str:
        try:
            return self.faker.food.ingredient()
        except AttributeError:
            return self.faker.word()

    def _generate_unique_email(self, name: str) -> str:
        """Generates a unique email."""
        try:
            base_email = name.split(' ')[0].lower().replace('.', '')
            return self.faker.unique.email(domain=f"comandalivre-test.com")
        except Exception:
            logging.warning("Faker unique email provider exhausted. Appending UUID.")
            return f"{name.replace(' ', '.')}.{uuid.uuid4()}@example.com"

    def generate_user_data(self) -> Dict[str, Any]:
        """Generates data for a new user registration."""
        name = self.faker.name()
        email = self._generate_unique_email(name)
        password = self.faker.password(length=12, special_chars=True, digits=True, upper_case=True, lower_case=True)
        return {
            "name": name,
            "email": email,
            "phone": self.faker.pt_br_cellphone(),
            "password": password,
            "passwordConfirmation": password
        }

    def generate_company_data(self) -> Dict[str, Any]:
        """Generates data for a new company."""
        name = self._sanitize_company_name(self.faker.unique.company())
        return {
            "name": name,
            "email": self.faker.unique.company_email(),
            "phone": self.faker.pt_br_cellphone(),
            "cnpj": self.faker.cnpj(), # Use faker's cnpj generator for pt_BR locale
            "description": self.faker.catch_phrase(),
            "type": "RESTAURANT",
        }

    def _sanitize_company_name(self, raw_name: str) -> str:
        """Sanitizes company name to match backend validation."""
        allowed = re.sub(r"[^a-zA-Z0-9À-ÿ '´`^~.,-]", " ", raw_name)
        normalized = re.sub(r"\s+", " ", allowed).strip()
        if len(normalized) < 3:
            normalized = f"{normalized} {self.faker.word().title()}".strip()
        return normalized[:100]

    def generate_product_data(self, company_id: str, category_id: str) -> Dict[str, Any]:
        """Generates data for a new product."""
        return {
            "name": self._food_dish(),
            "price": str(self.faker.pydecimal(left_digits=2, right_digits=2, positive=True, min_value=1, max_value=99)),
            "description": self.faker.sentence(nb_words=10),
            "ingredients": [self._food_ingredient() for _ in range(3)],
            "servesPersons": self.faker.random_int(min=1, max=4),
            "companyId": company_id,
            "categoryId": category_id,
            "availability": True,
        }

    def generate_table_data(self, company_id: str, name: str) -> Dict[str, Any]:
        """Generates data for a new table."""
        return {
            "name": name,
            "numPeople": self.faker.random_int(min=2, max=8),
            "description": f"Mesa com vista para {self.faker.street_name()}",
            "companyId": company_id,
        }

    def generate_table_update_data(self) -> Dict[str, Any]:
        """Generates data for updating a table."""
        return {
            "name": f"Mesa Atualizada {self.faker.random_int(min=1, max=9)}",
            "numPeople": self.faker.random_int(min=2, max=10),
            "description": self.faker.sentence(nb_words=6),
        }

    def generate_table_bulk_data(
        self,
        company_id: str,
        start: int,
        end: int,
        num_people: int = 4,
        description: Optional[str] = None,
    ) -> Dict[str, Any]:
        """Generates data for creating tables in bulk."""
        return {
            "companyId": company_id,
            "start": start,
            "end": end,
            "numPeople": num_people,
            "description": description or self.faker.sentence(nb_words=6),
        }

    def generate_employee_invite_data(self, company_id: str, role_id: str, email: str) -> Dict[str, Any]:
        """Generates data for an employee invitation."""
        return {
            "companyId": company_id,
            "roleId": role_id,
            "email": email,
        }

    def generate_command_data(self, table_id: str, employee_id: str) -> Dict[str, Any]:
        """Generates data for a new command."""
        return {
            "name": self.faker.name(),
            "numberOfPeople": self.faker.random_int(min=1, max=10),
            "tableId": table_id,
            "employeeId": employee_id,
        }

    def generate_order_data(self, command_id: str, product_ids: List[str]) -> Dict[str, Any]:
        """Generates data for a new order with multiple items."""
        items = [{"productId": pid, "notes": self.faker.sentence(nb_words=5)} for pid in product_ids]
        return {
            "commandId": command_id,
            "items": items,
        }

    def generate_order_notes(self, count: int) -> List[str]:
        """Generates a list of order notes."""
        return [self.faker.sentence(nb_words=5) for _ in range(count)]

    def generate_order_form(
        self,
        command_id: str,
        product_ids: List[str],
        notes: Optional[List[str]] = None,
    ) -> Dict[str, Any]:
        """Generates an order form with optional notes override."""
        if notes is None:
            notes = self.generate_order_notes(len(product_ids))
        items = [{"productId": pid, "notes": notes[idx]} for idx, pid in enumerate(product_ids)]
        return {
            "commandId": command_id,
            "items": items,
        }

    def generate_product_update_data(self, company_id: str, category_id: str) -> Dict[str, Any]:
        """Generates data for updating a product."""
        return {
            "name": f"{self._food_dish()} Atualizado",
            "price": str(self.faker.pydecimal(left_digits=2, right_digits=2, positive=True, min_value=1, max_value=99)),
            "description": self.faker.sentence(nb_words=8),
            "companyId": company_id,
            "categoryId": category_id,
            "availability": True,
        }

# --- Singleton Instance ---
data_generator = DataGenerator()

"""
Story: Public Routes Accessibility and Chained Logic

This story verifies that key public-facing endpoints are accessible and
that data can be chained between them. It follows a logical sequence:
1. Fetch a list of public companies.
2. If companies are found, use an ID from the list to fetch a single company.
3. Use that same company ID to fetch its products.
4. If products are found, use an ID from that list to fetch a single product.

If a step fails or returns no data, subsequent dependent steps are skipped.
"""
import logging
from typing import List, Dict, Any, Optional

from rich.console import Console
from rich.panel import Panel
from rich.table import Table

from data_test.core.api_client import api_client

console = Console()

class TestResult:
    """A simple class to hold the result of a single test step."""
    def __init__(self, endpoint: str, status: str, status_code: str = "-"):
        self.endpoint = endpoint
        self.status = status
        self.status_code = status_code

class PublicRoutesStory:
    """Manages the state and execution of the public routes story."""

    def __init__(self):
        self.company_id: Optional[str] = None
        self.product_id: Optional[str] = None
        self.results: List[TestResult] = []

    def run(self):
        """Executes all steps of the story in sequence."""
        console.print(Panel("[bold cyan]Executing Story: Public Routes Chained Logic[/bold cyan]", expand=False))
        
        self._test_get_companies()
        self._test_get_company_by_id()
        self._test_get_products_for_company()
        self._test_get_product_by_id()
        
        self._print_summary()

    def _test_get_companies(self):
        """Step 1: Fetch public companies."""
        endpoint = "GET /company/companies"
        logging.info(f"Executing: {endpoint}")
        response = api_client.get_public_companies(params={"page": 0, "size": 1})
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                content = data.get("content", [])
                if content:
                    self.company_id = content[0].get("id")
                    logging.info(f"SUCCESS: Found company with ID: {self.company_id}")
                    self.results.append(TestResult(endpoint, "[bold green]Success[/]", str(response.status_code)))
                else:
                    logging.warning("SKIPPED: GET /company/companies returned no content.")
                    self.results.append(TestResult(endpoint, "[yellow]Skipped (No Content)[/]", str(response.status_code)))
            except ValueError:
                logging.error("FAILURE: Failed to parse JSON from GET /company/companies.")
                self.results.append(TestResult(endpoint, "[bold red]Failure (JSON Error)[/]", str(response.status_code)))
        elif response and response.status_code == 404:
             logging.warning("SKIPPED: GET /company/companies returned 404 Not Found.")
             self.results.append(TestResult(endpoint, "[yellow]Skipped (Not Found)[/]", str(response.status_code)))
        else:
            status_code = str(response.status_code) if response else "N/A"
            logging.error(f"FAILURE: {endpoint} failed with status {status_code}.")
            self.results.append(TestResult(endpoint, "[bold red]Failure[/]", status_code))

    def _test_get_company_by_id(self):
        """Step 2: Fetch a single company if an ID was found."""
        endpoint = "GET /company/companies/{id}"
        if not self.company_id:
            logging.warning(f"SKIPPED: {endpoint} because no company ID is available.")
            self.results.append(TestResult(endpoint, "[yellow]Skipped[/]"))
            return

        logging.info(f"Executing: {endpoint} with ID {self.company_id}")
        response = api_client.get_public_company_by_id(self.company_id)
        
        if response and response.status_code == 200:
            logging.info(f"SUCCESS: {endpoint} returned 200.")
            self.results.append(TestResult(endpoint, "[bold green]Success[/]", str(response.status_code)))
        else:
            status_code = str(response.status_code) if response else "N/A"
            logging.error(f"FAILURE: {endpoint} failed with status {status_code}.")
            self.results.append(TestResult(endpoint, "[bold red]Failure[/]", status_code))

    def _test_get_products_for_company(self):
        """Step 3: Fetch products for the company if an ID is available."""
        endpoint = "GET /comandalivre/products?companyId={id}"
        if not self.company_id:
            logging.warning(f"SKIPPED: {endpoint} because no company ID is available.")
            self.results.append(TestResult(endpoint, "[yellow]Skipped[/]"))
            return

        logging.info(f"Executing: {endpoint} with company ID {self.company_id}")
        response = api_client.get_public_products(self.company_id)

        if response and response.status_code == 200:
            try:
                data = response.json()
                content = data.get("content", [])
                if content:
                    self.product_id = content[0].get("id")
                    logging.info(f"SUCCESS: Found product with ID: {self.product_id}")
                    self.results.append(TestResult(endpoint, "[bold green]Success[/]", str(response.status_code)))
                else:
                    logging.warning("SKIPPED: GET /comandalivre/products returned no content.")
                    self.results.append(TestResult(endpoint, "[yellow]Skipped (No Content)[/]", str(response.status_code)))
            except ValueError:
                logging.error("FAILURE: Failed to parse JSON from GET /comandalivre/products.")
                self.results.append(TestResult(endpoint, "[bold red]Failure (JSON Error)[/]", str(response.status_code)))
        elif response and response.status_code == 404:
             logging.warning("SKIPPED: GET /comandalivre/products returned 404 Not Found.")
             self.results.append(TestResult(endpoint, "[yellow]Skipped (Not Found)[/]", str(response.status_code)))
        else:
            status_code = str(response.status_code) if response else "N/A"
            logging.error(f"FAILURE: {endpoint} failed with status {status_code}.")
            self.results.append(TestResult(endpoint, "[bold red]Failure[/]", status_code))
            
    def _test_get_product_by_id(self):
        """Step 4: Fetch a single product if an ID was found."""
        endpoint = "GET /comandalivre/products/{id}"
        if not self.product_id:
            logging.warning(f"SKIPPED: {endpoint} because no product ID is available.")
            self.results.append(TestResult(endpoint, "[yellow]Skipped[/]"))
            return

        logging.info(f"Executing: {endpoint} with ID {self.product_id}")
        response = api_client.get_public_product_by_id(self.product_id)
        
        if response and response.status_code == 200:
            logging.info(f"SUCCESS: {endpoint} returned 200.")
            self.results.append(TestResult(endpoint, "[bold green]Success[/]", str(response.status_code)))
        else:
            status_code = str(response.status_code) if response else "N/A"
            logging.error(f"FAILURE: {endpoint} failed with status {status_code}.")
            self.results.append(TestResult(endpoint, "[bold red]Failure[/]", status_code))

    def _print_summary(self):
        """Prints the final results table."""
        table = Table(title="Public Routes Test Summary")
        table.add_column("Endpoint", justify="left", style="cyan", no_wrap=True)
        table.add_column("Status Code", justify="center", style="magenta")
        table.add_column("Result", justify="center")

        for result in self.results:
            table.add_row(result.endpoint, result.status_code, result.status)

        console.print(table)

def run_story():
    """
    Entry point for the story, called by the CLI.
    """
    story = PublicRoutesStory()
    story.run()
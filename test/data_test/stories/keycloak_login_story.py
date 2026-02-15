"""
Story: Keycloak Direct Grant Authentication

This story verifies that the testing framework can successfully authenticate
against the Keycloak server using the Direct Access Grant flow with a
pre-configured test user and backend client.
"""
import logging

from rich.console import Console
from rich.panel import Panel
from rich.syntax import Syntax

from data_test.core.api_client import api_client
from data_test.core.config import TEST_USER_USERNAME

console = Console()


def run_story():
    """
    Executes the Keycloak login test story.
    """
    console.print(Panel("[bold cyan]Executing Story: Keycloak Direct Grant Authentication[/bold cyan]", expand=False))

    logging.info("Attempting to authenticate with Keycloak...")
    console.print(f"  - Authenticating user [yellow]{TEST_USER_USERNAME}[/yellow] against Keycloak...")

    response = api_client.authenticate_with_keycloak()

    if response and response.status_code == 200:
        try:
            token_data = response.json()
            access_token = token_data.get("access_token")


            if access_token:
                logging.info("SUCCESS: Keycloak authentication successful. Token received.")
                console.print("    [bold green]SUCCESS[/]: Authentication successful.")

                # Display a snippet of the token for verification
                console.print(f"      [dim]Bearer Token Snippet:[/] {access_token}")

                api_client.set_token(access_token)
                api_client.auth()

                # Pretty-print the decoded token claims (header.payload.signature)
                try:
                    import jwt
                    import json
                    decoded_payload = jwt.decode(access_token, options={"verify_signature": False})
                    pretty_payload = json.dumps(decoded_payload, indent=2)
                    console.print(
                        Panel(
                            Syntax(pretty_payload, "json", theme="monokai", line_numbers=True),
                            title="Decoded JWT Payload",
                            border_style="blue",
                            expand=False
                        )
                    )
                except (ImportError, Exception) as e:
                     logging.warning(f"Could not decode JWT token for pretty printing: {e}. PyJWT not installed?")
                     console.print("[yellow]Note:[/] To see the decoded token payload, run `uv pip install PyJWT`.")

            else:
                logging.error("FAILURE: Keycloak response is 200 OK but does not contain 'access_token'.")
                console.print("    [bold red]FAILURE[/]: Response OK, but no token found in body.")
                console.print(f"      [dim]Response Body:[/] {response.text}")

        except ValueError:
            logging.error("FAILURE: Could not decode JSON response from Keycloak.")
            console.print("    [bold red]FAILURE[/]: Could not decode JSON from response.")
            console.print(f"      [dim]Response Body:[/] {response.text}")

    elif response:
        logging.error(f"FAILURE: Keycloak authentication failed with status {response.status_code}.")
        console.print(f"    [bold red]FAILURE[/]: Authentication request failed with status code {response.status_code}.")
        console.print(f"      [dim]Response Body:[/] {response.text}")

    else:
        logging.critical("CRITICAL: No response from Keycloak server. Is it running?")
        console.print("    [bold red]CRITICAL FAILURE[/]: No response from Keycloak server.")
        console.print("      Check if the Keycloak container is running on the configured port (default: 8082).")


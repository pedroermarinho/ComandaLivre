"""
Configuration settings for the data generation and API integration test script.

This module centralizes settings such as API endpoints, test run parameters,
and logging configurations. It's recommended to use environment variables
for sensitive or environment-specific values.
"""
import logging
import os
from pathlib import Path

# --- General Settings ---
# Use Path for robust path handling. BASE_DIR points to the 'data_test' directory.
BASE_DIR = Path(__file__).resolve().parent.parent

# --- API Configuration ---
# API base URL can be overridden by an environment variable.
# Based on the Java project, the default port is 8080.
API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080/api/v1")

# Specific endpoints derived from the project structure and test files.
# Using the public route from PublicRoutesStoryITest.kt for the health check.
HEALTH_CHECK_ENDPOINT = f"{API_BASE_URL}/company/companies"

# --- Keycloak Configuration ---
# Details from devops/keycloak/realm-export.json
KEYCLOAK_BASE_URL = os.getenv("KEYCLOAK_BASE_URL", "http://localhost:8090")
KEYCLOAK_REALM = "comandalivre"
KEYCLOAK_TOKEN_ENDPOINT = f"{KEYCLOAK_BASE_URL}/realms/{KEYCLOAK_REALM}/protocol/openid-connect/token"

# Admin access for dynamic user creation
KEYCLOAK_ADMIN_REALM = "master"
KEYCLOAK_ADMIN_CLIENT_ID = "admin-cli"
KEYCLOAK_ADMIN_USERNAME = os.getenv("KEYCLOAK_ADMIN_USERNAME", "admin")
KEYCLOAK_ADMIN_PASSWORD = os.getenv("KEYCLOAK_ADMIN_PASSWORD", "admin")
KEYCLOAK_ADMIN_TOKEN_ENDPOINT = (
    f"{KEYCLOAK_BASE_URL}/realms/{KEYCLOAK_ADMIN_REALM}/protocol/openid-connect/token"
)

# Backend client with direct access grant enabled
KEYCLOAK_CLIENT_ID = "backend"
KEYCLOAK_CLIENT_SECRET = "backend-secret-fixa-123"

# Pre-configured test user
TEST_USER_USERNAME = "teste@comandalivre.com.br"
TEST_USER_PASSWORD = "teste123"



# --- Test Run Configuration ---
# The number of iterations can be configured via environment variables.
ITERATION_COUNT = int(os.getenv("ITERATION_COUNT", 1))

# --- Logging Configuration ---
LOG_DIR = BASE_DIR.parent / "logs"
LOG_FILE = LOG_DIR / "integration_test.log"
LOG_LEVEL = logging.INFO

# --- Faker Configuration ---
FAKER_LOCALE = "pt_BR"


def setup_logging():
    """Configures the logging for the script."""
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    logging.basicConfig(
        level=LOG_LEVEL,
        format="%(asctime)s - %(levelname)s - %(module)s - %(message)s",
        handlers=[
            logging.FileHandler(LOG_FILE),
            logging.StreamHandler()
        ]
    )

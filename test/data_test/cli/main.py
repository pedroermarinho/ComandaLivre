"""
Main CLI application for running integration test stories.

This script uses Typer to create a command-line interface that allows
users to discover and execute different test "stories" in a modular way.
"""
import importlib
import logging
import time
from pathlib import Path
from typing import List, Optional

import typer
from rich.console import Console
from rich.panel import Panel
from rich.prompt import Prompt

from data_test.core.config import setup_logging

# --- Typer App Initialization ---
app = typer.Typer(
    name="data-test-cli",
    help="A CLI for running modular integration test stories against the ComandaLivre API.",
    add_completion=False
)
console = Console()

# --- Story Discovery ---
STORY_DIR = Path(__file__).resolve().parent.parent / "stories"

def get_available_stories() -> List[str]:
    """Dynamically discovers available story files."""
    stories = [
        f.stem for f in STORY_DIR.glob("*.py") if not f.name.startswith("__")
    ]
    return stories

# --- CLI Commands ---

@app.command()
def run(
    story_name: Optional[str] = typer.Argument(
        None,
        help="The name of the story to run (e.g., 'public_routes_story').",
        autocompletion=get_available_stories
    ),
    iterations: int = typer.Option(1, "--iterations", "-i", help="Number of times to run the story."),
    loop: bool = typer.Option(False, "--loop", "-l", help="Run the story in an infinite loop."),
    delay: float = typer.Option(1.0, "--delay", "-d", help="Delay in seconds between loop iterations."),
):
    """
    Discover and run a specific test story.
    """
    setup_logging()
    console.print(Panel("[bold green]ComandaLivre - Integration Test CLI[/bold green]", expand=False))

    # --- Story Selection ---
    available_stories = get_available_stories()
    if not available_stories:
        console.print("[bold red]Error:[/] No stories found in the 'stories' directory.")
        raise typer.Exit(code=1)

    if story_name is None:
        console.print("\n[bold yellow]Please choose a story to run:[/bold yellow]")
        for i, story in enumerate(available_stories, 1):
            console.print(f"  [cyan]{i}[/]. {story}")
        choice = Prompt.ask("\nEnter the number of your choice", choices=[str(i) for i in range(1, len(available_stories) + 1)])
        story_name = available_stories[int(choice) - 1]

    if story_name not in available_stories:
        console.print(f"[bold red]Error:[/] Story '{story_name}' not found.")
        console.print(f"Available stories: {', '.join(available_stories)}")
        raise typer.Exit(code=1)

    # --- Story Execution ---
    try:
        story_module = importlib.import_module(f"data_test.stories.{story_name}")
        logging.info(f"Successfully imported story: {story_name}")
    except ImportError as e:
        logging.error(f"Failed to import story '{story_name}': {e}")
        console.print(f"[bold red]Error:[/] Could not import story module '{story_name}'.")
        raise typer.Exit(code=1)

    # --- Execution Loop ---
    if loop:
        console.print(f"\n[bold]Running story '{story_name}' in a loop. Press Ctrl+C to stop.[/bold]")
        run_count = 0
        while True:
            try:
                run_count += 1
                console.print(f"\n--- [blue]Loop Iteration: {run_count}[/blue] ---")
                story_module.run_story()
                time.sleep(delay)
            except KeyboardInterrupt:
                console.print("\n[yellow]Loop stopped by user.[/yellow]")
                break
    else:
        console.print(f"\n[bold]Running story '{story_name}' for {iterations} iteration(s).[/bold]")
        for i in range(iterations):
            console.print(f"\n--- [blue]Iteration: {i + 1} of {iterations}[/blue] ---")
            story_module.run_story()

    console.print(f"\n[bold green]Finished running story '{story_name}'.[/bold green]")


@app.command()
def list_stories():
    """
    List all available test stories.
    """
    console.print("[bold green]Available Test Stories:[/bold green]")
    stories = get_available_stories()
    if not stories:
        console.print("  No stories found.")
        return
    for story in stories:
        console.print(f"  - [cyan]{story}[/cyan]")


if __name__ == "__main__":
    app()

#!/bin/bash
# Setup script to install git hooks from .git-hooks directory

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

GIT_HOOKS_DIR=".git/hooks"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${YELLOW}Installing git hooks...${NC}"

# Create symlinks for each hook file
for hook in "$SCRIPT_DIR"/*; do
    if [ -f "$hook" ] && [ "$(basename "$hook")" != "setup.sh" ]; then
        hook_name=$(basename "$hook")
        target="$GIT_HOOKS_DIR/$hook_name"

        # Remove existing hook if present
        if [ -e "$target" ]; then
            rm "$target"
        fi

        # Create symlink
        ln -s "../../.git-hooks/$hook_name" "$target"
        chmod +x "$target"
        echo -e "${GREEN}✓ Installed $hook_name${NC}"
    fi
done

echo -e "${GREEN}✓ Git hooks installed successfully!${NC}"
echo ""
echo "The pre-commit hook will now run:"
echo "  • Detekt (static analysis)"
echo "  • ArchUnit architecture tests"
echo ""
echo "To skip the hook for a commit, use: git commit --no-verify"

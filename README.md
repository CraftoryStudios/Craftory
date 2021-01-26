# Craftory Core
Core for all Craftory plugins and addons. Brings mod like features to vanilla Minecraft, all that's needed is a Paper or Spigot server to run on, and a resource pack.

## Developer Guide
This guide will take you through the steps required to contribute to Craftory Core.

### Local Setup
Make sure you first set up Git Hooks, this will ensure you follow the git commit conversion.
```sh
git config core.hooksPath .githooks
```

Run this command in a terminal in the root directory of this project.

Some GUI based Git clients might not work with this, in this case you can copy the file commit-msg found in project-root/.githooks to project-root/.git/hooks folder (this folder may be hidden).

### Making a Git Commit
When making a commit we use a convention for all our commit messages which can be found here [Conventional Commits Specification](https://www.conventionalcommits.org/en/v1.0.0/).

We do this for automatic generation of changelogs used as part of automatic deployment to multiple places including: GitHub, Spigot and Minecraft Servers.

To ensure you are following it correctly we encourage you to set up Git Hooks, which will warn you if you make a mistake.
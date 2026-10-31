---
title: Release guide
description: Release the Wanaku Capabilities Java SDK from an X.Y.x branch, with the branch setup, the version variables, and the release workflow command.
---

# Release Guide

Releases are cut from release branches following the `X.Y.x` naming convention (e.g., `0.1.x`, `0.2.x`).

## Create the release branch (first release of a minor version only)

```shell
git checkout main
git checkout -b 0.3.x
git push upstream 0.3.x
```

## Set the versions

```shell
export RELEASE_BRANCH=0.3.x
export CURRENT_DEVELOPMENT_VERSION=0.3.0
export NEXT_DEVELOPMENT_VERSION=0.3.1
```

## Trigger the release

```shell
gh workflow run release -r ${RELEASE_BRANCH} -f releaseBranch=${RELEASE_BRANCH} -f currentDevelopmentVersion=${CURRENT_DEVELOPMENT_VERSION} -f nextDevelopmentVersion=${NEXT_DEVELOPMENT_VERSION}
```

> [!NOTE]
> The `-r` flag tells GitHub to run the workflow from the release branch.
> The `releaseBranch` input tells the workflow which branch to check out and push
> version bump commits to.

## Next steps

- [Contributing](index.md): build and test the SDK.
- [Write SDK documentation](documentation.md): follow the page conventions.

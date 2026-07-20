# Release Guide

Releases are cut from release branches following the `X.Y.x` naming convention (e.g., `0.1.x`, `0.2.x`).

## Create the release branch (first release of a minor version only)

```shell
git checkout main
git checkout -b 0.2.x
git push upstream 0.2.x
```

## Set the versions

```shell
export RELEASE_BRANCH=0.2.x
export CURRENT_DEVELOPMENT_VERSION=0.2.1
export NEXT_DEVELOPMENT_VERSION=0.2.2
```

## Trigger the release

```shell
gh workflow run release -r ${RELEASE_BRANCH} -f releaseBranch=${RELEASE_BRANCH} -f currentDevelopmentVersion=${CURRENT_DEVELOPMENT_VERSION} -f nextDevelopmentVersion=${NEXT_DEVELOPMENT_VERSION}
```

> **Note:** The `-r` flag tells GitHub to run the workflow from the release branch.
> The `releaseBranch` input tells the workflow which branch to check out and push
> version bump commits to.

# Release Guide

Export the variables:

```shell
export CURRENT_DEVELOPMENT_VERSION=0.0.9
export NEXT_DEVELOPMENT_VERSION=0.1.0
```

Trigger the release automation: 

```shell
gh workflow run release -f currentDevelopmentVersion=${CURRENT_DEVELOPMENT_VERSION} -f nextDevelopmentVersion=${NEXT_DEVELOPMENT_VERSION}
```
# How to contribute

This project uses the [Git flow](http://nvie.com/posts/a-successful-git-branching-model/) workflow
and [semantic versioning](http://semver.org/) for the versioning.

## Steps

1. Open an issue to detail what you would like to add/modify/fix in the project.
2. Fork the repository.
3. In case it is a fix, create a new hotfix branch and in case of a new feature create a new feature
branch.
4. Implement the code changes in the branch of your fork.
5. Create a pull request. The pull request will be accepted if and only if the coding conventions
are properly followed.

## Coding conventions

The project follows a standard for coding.

### Style

The code is formatted according to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
and the build will fail if the code is not properly formatted.

### Static analysis

The project is configured to use [Maven PMD plugin](https://maven.apache.org/plugins/maven-pmd-plugin/)
for static analysis. It is currently running with the default rules. When rule violations are 
found, the PMD plugin fails the build.

### Test Coverage

Your code has to be fully tested and keep the 100% of line and branch coverage.

## Git flow

This section shows how to create a feature or a hotfix for being conformed to the Git flow workflow.

### Feature

### Create new feature branch

Feature branch must reflect Github issue number and have meaningful name.

```mvn jgitflow:feature-start```

Push feature branch into remote repository.

```git push origin feature/name```

### Work on feature branch

```
git add new_file1 new_file2 ...
git commit -a
git push origin feature/name
```

### Finish feature branch

Ensure your local develop branch is up to date.

```
git checkout develop
git pull origin develop
```

To avoid conflicts during finishing feature branch, ensure that all changes from develop are merged
to the feature branch.

```
git checkout feature/name
git pull origin develop
git status
```

Resolve all conflicts (if any) and commit changes.

``
git commit -a -m "Conflicts resolved"`
```

Finish the feature.

```
mvn jgitflow:feature-finish
```

Push changes from develop into remote repository.

``
git push origin develop
git push origin :feature/name
```

### Hotfix

### Create new hotfix branch

Hotfix branch must reflect Github issue number and have meaningful name.

```mvn jgitflow:hotfix-start```

Push hotfix branch into remote repository.

```git push origin hotfix/name```

### Work on hotfix branch

```
git add new_file1 new_file2 ...
git commit -a
git push origin hotfix/name
```

### Finish hotfix branch

Ensure your local develop branch is up to date.

```
git checkout develop
git pull origin develop
```

To avoid conflicts during finishing hotfix branch, ensure that all changes from develop are merged
to the hotfix branch.

```
git checkout hotfix/name
git pull origin develop
git status
```

Resolve all conflicts (if any) and commit changes.

``
git commit -a -m "Conflicts resolved"`
```

Finish the feature.

```
mvn jgitflow:hotfix-finish
```

Push changes from develop into remote repository.

``
git push origin develop
git push origin :hotfix/name
````
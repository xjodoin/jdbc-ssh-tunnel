# Contributing to JDBC SSH Tunnel

First off, thank you for considering contributing to the **JDBC SSH Tunnel** project! Your time and effort are greatly appreciated, and we value every contribution, whether it's reporting a bug, suggesting an enhancement, or writing code.

This guide will help you get started with contributing to the project.

## Table of Contents

- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Submitting Changes](#submitting-changes)
- [Development Guidelines](#development-guidelines)
  - [Code Style](#code-style)
  - [Testing](#testing)
  - [Documentation](#documentation)
- [Code of Conduct](#code-of-conduct)
- [License](#license)

## Getting Started

To contribute to the project, you'll need to:

1. **Fork the Repository**: Click the "Fork" button on the project's GitHub page to create your own copy of the repository.

2. **Clone Your Fork**: Clone your forked repository to your local machine:

   ```bash
   git clone https://github.com/your-username/jdbc-ssh-tunnel.git
   ```

3. **Set Upstream Remote**: Add the original repository as an upstream remote to keep your fork up-to-date:

   ```bash
   git remote add upstream https://github.com/xjodoin/jdbc-ssh-tunnel.git
   ```

4. **Create a Branch**: Create a new branch for your feature or bugfix:

   ```bash
   git checkout -b feature/your-feature-name
   ```

## How to Contribute

### Reporting Bugs

If you encounter any issues or bugs, please report them by opening an [issue](https://github.com/xjodoin/jdbc-ssh-tunnel/issues) on GitHub. When reporting a bug, please include:

- A clear and descriptive title.
- Steps to reproduce the issue.
- Expected and actual results.
- Any relevant logs or screenshots.

### Suggesting Enhancements

We welcome suggestions for new features or improvements. To suggest an enhancement:

- Open a new [issue](https://github.com/xjodoin/jdbc-ssh-tunnel/issues) with the label "enhancement".
- Provide a detailed description of the proposed feature.
- Explain why it would be beneficial to the project.

### Submitting Changes

When you're ready to submit your changes:

1. **Commit Your Changes**: Make sure your commit messages are clear and descriptive.

   ```bash
   git add .
   git commit -m "Add feature XYZ"
   ```

2. **Push to Your Fork**:

   ```bash
   git push origin feature/your-feature-name
   ```

3. **Open a Pull Request**:

   - Go to your fork on GitHub.
   - Click the "Compare & pull request" button.
   - Provide a clear description of your changes.
   - Submit the pull request to the `master` branch of the original repository.

**Note**: Ensure your pull request passes all automated checks and includes tests if applicable.

## Development Guidelines

### Code Style

- Follow the existing coding style of the project.
- Use meaningful variable and method names.
- Keep code clean and well-organized.
- Avoid unnecessary complexity.

### Testing

- Write unit tests for new features and bug fixes.
- Ensure all existing tests pass before submitting.
- Use a consistent testing framework (e.g., JUnit).

### Documentation

- Update documentation to reflect any changes.
- Comment your code where necessary.
- Maintain clarity and conciseness in documentation.

## Code of Conduct

We are committed to fostering a welcoming and inclusive community. By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## License

By contributing to **JDBC SSH Tunnel**, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE.txt).

# Contributing to SpatialAudio HeadTracker Test

Thank you for your interest in contributing to the SpatialAudio HeadTracker Test project! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for all contributors.

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check the existing issues to avoid duplicates. When creating a bug report, include:

- **Device Information**: Device model, Android version, and spatial audio support
- **Clear Description**: What you expected vs. what actually happened
- **Steps to Reproduce**: Detailed steps to reproduce the issue
- **Screenshots**: If applicable, add screenshots to help explain the problem
- **Logcat Output**: Relevant error messages or stack traces

### Suggesting Enhancements

Enhancement suggestions are welcome! Please provide:

- **Use Case**: Clear description of the problem or need
- **Proposed Solution**: How you envision the enhancement working
- **Alternatives**: Any alternative solutions you've considered
- **Impact**: Who would benefit from this enhancement

### Pull Requests

1. **Fork the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SpatialAudio-HeadTracker-Test.git
   ```

2. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Your Changes**
   - Follow the coding standards below
   - Write clear, concise commit messages
   - Add tests if applicable

4. **Test Your Changes**
   - Test on devices with and without spatial audio support
   - Verify the app builds without errors
   - Check for lint warnings

5. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Add feature: your feature description"
   ```

6. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request**
   - Provide a clear description of the changes
   - Reference any related issues
   - Wait for review and address any feedback

## Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for variable and function names
- Use PascalCase for class names
- Maximum line length: 120 characters

### Code Documentation

- Add KDoc comments for all public classes, functions, and properties
- Include parameter descriptions and return value documentation
- Example:
  ```kotlin
  /**
   * Brief description of the function.
   *
   * @param paramName Description of the parameter
   * @return Description of the return value
   */
  fun functionName(paramName: Type): ReturnType {
      // Implementation
  }
  ```

### Android Best Practices

- Follow [Android's Architecture Guidelines](https://developer.android.com/topic/architecture)
- Use Jetpack Compose best practices
- Implement proper lifecycle management
- Handle configuration changes appropriately
- Use string resources for all user-facing text
- Implement proper error handling

### Compose Guidelines

- Keep composables small and focused
- Use `remember` for state that survives recomposition
- Use `LaunchedEffect` for side effects
- Use `DisposableEffect` for resource cleanup
- Avoid using `stringResource` outside of composable functions

### Resource Management

- Always release audio resources properly
- Use try-catch blocks for operations that may fail
- Clean up threads and background tasks
- Handle null cases appropriately

## Testing Guidelines

### Manual Testing

Before submitting a PR, test on:
- Devices with spatial audio support
- Devices without spatial audio support
- Different Android versions (API 33+)
- Different audio output devices (speakers, wired headphones, Bluetooth)

### Testing Checklist

- [ ] App builds without errors
- [ ] No lint warnings for modified code
- [ ] Spatial audio status displays correctly
- [ ] Audio playback starts and stops properly
- [ ] UI responds to device capabilities
- [ ] No memory leaks or resource issues
- [ ] Proper error handling for edge cases

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/headtrackertest/
│   │   ├── MainActivity.kt          # Main activity and core logic
│   │   └── ui/theme/               # Material3 theme files
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml         # All string resources
│   │   │   ├── colors.xml          # Color definitions
│   │   │   └── themes.xml          # Theme configurations
│   │   └── mipmap/                 # App launcher icons
│   └── AndroidManifest.xml         # App manifest
└── build.gradle.kts                # App build configuration
```

## Commit Message Guidelines

Use clear and descriptive commit messages:

- Use the imperative mood ("Add feature" not "Added feature")
- First line should be 50 characters or less
- Provide detailed description in the body if needed
- Reference issues with `#issue-number`

Example:
```
Add volume control feature

- Implement volume slider in UI
- Add volume state management
- Update documentation

Fixes #123
```

## Questions or Need Help?

If you have questions or need help:
- Check the [README](README.md) for basic information
- Review existing issues and pull requests
- Open a new issue with the "question" label

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (see [LICENSE](LICENSE) file).

Thank you for contributing to SpatialAudio HeadTracker Test!

[![pipeline status](https://lc.llnl.gov/gitlab/rda/gov.llnl.utility/badges/ernie/pipeline.svg)](https://lc.llnl.gov/gitlab/rda/gov.llnl.utility/commits/ernie)
[![coverage report](https://lc.llnl.gov/gitlab/rda/gov.llnl.utility/badges/ernie/coverage.svg)](https://lc.llnl.gov/gitlab/rda/gov.llnl.utility/commits/ernie)

# gov.llnl.utility

## Overview

The `gov.llnl.utility` package is a comprehensive Java utility library developed by Lawrence Livermore National Laboratory (LLNL). It provides a wide range of utility classes and functions that extend the standard Java libraries with additional functionality for common programming tasks. This package serves as a foundation for many other LLNL projects, including the RADSIM simulation framework.

## Key Features

- **Extensive Utility Classes**: Collection of helper methods for strings, files, arrays, collections, and more
- **XML Support**: Tools for XML parsing, binding, and schema management
- **Streaming Utilities**: Enhanced input/output stream handling and processing
- **Resource Management**: Package resource handling and management
- **Reflection Utilities**: Type introspection and manipulation tools
- **Logging Enhancements**: Extensions to Java logging framework
- **File System Operations**: Utilities for file operations, including compressed files
- **Date and Time Utilities**: Helper methods for working with dates, times and durations
- **Collection Extensions**: Additional collection types and utilities

## Main Components

### Core Utilities

The library includes numerous utility classes for common operations:

- **StringUtilities**: String manipulation and processing
- **FileUtilities**: File system operations with compression support
- **ArrayUtilities**: Array manipulation methods
- **CollectionUtilities**: Collection manipulation and conversion
- **DateUtilities**: Date and time handling
- **DurationUtilities**: Working with time durations
- **PathUtilities**: Path manipulation and resolution
- **HashUtilities**: Hashing and checksum operations
- **ListUtilities**: List-specific operations

### XML Support

- **PackageResource**: Schema management and resource handling
- **UtilityPackage**: Schema resources for XML binding

### Data Structures

- **ArrayMap**: Map implementation backed by arrays
- **LimitedSizeQueue**: Queue with a maximum capacity
- **Pair**: Generic pair/tuple implementation
- **FutureQueue**: Queue for managing asynchronous operations
- **IteratorInputStream**: Convert iterators to input streams

### I/O Support

- **ByteBufferInputStream**: Stream from byte buffers
- **InputStreamUtilities**: Input stream operations
- **Tokenizer**: Text tokenization

### Reflection and Annotations

- **AnnotationUtilities**: Annotation processing
- **ClassUtilities**: Class operations and introspection
- **ReflectionUtilities**: Reflection operations

### Logging

- **LogFormatter**: Custom log formatting
- **LoggerStream**: Stream-based logging
- **LoggerUtilities**: Logger configuration and setup

## Usage Examples

### String Utilities

```java
import gov.llnl.utility.StringUtilities;

// Join strings with a delimiter
String[] parts = {"Hello", "world", "example"};
String joined = StringUtilities.join(parts, ", ");  // "Hello, world, example"
```

### File Operations

```java
import gov.llnl.utility.FileUtilities;
import java.nio.file.Path;
import java.nio.file.Paths;

// Create input stream with automatic compression detection
Path file = Paths.get("data.txt.gz");
try (InputStream is = FileUtilities.createInputStream(file)) {
    // Process compressed stream transparently
}
```

### Collection Utilities

```java
import gov.llnl.utility.CollectionUtilities;
import java.util.Arrays;
import java.util.List;

// Find first matching element
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Integer found = CollectionUtilities.find(numbers, n -> n > 3);  // Returns 4
```

### XML Package Resources

```java
import gov.llnl.utility.UtilityPackage;

// Access the utility package singleton
UtilityPackage pkg = UtilityPackage.getInstance();

// Configure logging
pkg.enableLog(Level.INFO);
```

## Integration with RADSIM

This package serves as a foundation for the RADSIM simulation framework:

1. It provides essential utility functions used throughout the framework
2. Its XML binding capabilities support configuration and data serialization
3. The logging utilities enable consistent diagnostics and monitoring
4. Its resource management facilities help organize the modular structure of RADSIM

## Package Structure

The package is organized into public and private components:

- **Public API**: Classes in `src/public/gov/llnl/utility/` are intended for general use
- **Private Implementation**: Classes in `src/private/gov/llnl/utility/` are internal implementation details
- **Tests**: Unit tests in `test/gov/llnl/utility/` provide test coverage

## Dependencies

The package has minimal external dependencies, primarily relying on:

- Java Standard Library
- Java XML APIs

## Testing

The package includes comprehensive unit tests using TestNG:

```bash
# Run tests
cd src/gov.llnl.utility
ant test
```

## License

Copyright 2016-2026, Lawrence Livermore National Security, LLC.
All rights reserved.

Terms and conditions are given in the "Notice" file.
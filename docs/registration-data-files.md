# Registration Process and Data File Persistence

This document explains how the registration process uses data files to persist the ID of capabilities being registered with the Wanaku Router.

## Overview

When a capability service registers with the Wanaku Router, it receives a unique UUID. This ID must be persisted locally to:
- Maintain identity across service restarts
- Avoid duplicate registrations
- Enable proper heartbeat and state update operations

## Data File Format

### File Location and Naming

Data files follow this naming convention:
```
{dataDir}/{serviceName}.wanaku.dat
```

For example, a tool invoker service named "my-tool" with data directory `/var/wanaku` stores its ID at:
```
/var/wanaku/my-tool.wanaku.dat
```

### Binary Structure

The data file uses a fixed-size binary format totaling 64 bytes:

```
+------------------------+
|    FileHeader (24B)    |
+------------------------+
|   ServiceEntry (40B)   |
+------------------------+
```

#### FileHeader (24 bytes)

| Field          | Size    | Description                           |
|----------------|---------|---------------------------------------|
| Format name    | 6 bytes | Fixed value: "wanaku"                 |
| File version   | 4 bytes | Integer, currently 1                  |
| Service type   | 4 bytes | Integer code for service type         |
| Padding        | 2 bytes | Reserved                              |
| Reserved       | 4 bytes | Future use                            |
| Additional pad | 4 bytes | Alignment                             |

#### ServiceEntry (40 bytes)

| Field      | Size     | Description                    |
|------------|----------|--------------------------------|
| Service ID | 36 bytes | UUID string assigned by router |
| Padding    | 4 bytes  | Alignment                      |

#### Service Type Codes

| Service Type            | Code |
|-------------------------|------|
| `tool-invoker`          | 1    |
| `resource-provider`     | 2    |
| `multi-capability`      | 3    |
| `code-execution-engine` | 3    |

## Registration Flow

### Phase 1: Initialization Check

During `ZeroDepRegistrationManager` construction:

```java
instanceDataManager = new InstanceDataManager(config.getDataDir(), target.getServiceName());

if (instanceDataManager.dataFileExists()) {
    // Load existing ID
    ServiceEntry entry = instanceDataManager.readEntry();
    if (entry != null) {
        target.setId(entry.getId());
    }
} else {
    // First run - create directory structure
    instanceDataManager.createDataDirectory();
}
```

Key classes:
- `InstanceDataManager`: Central persistence coordinator
- `InstanceDataReader`: Binary file reader

### Phase 2: Registration with Router

When no existing ID is found:

1. `ZeroDepRegistrationManager.tryRegistering()` sends HTTP POST to router
2. Router assigns UUID and returns it in response
3. ID is immediately persisted:

```java
// Extract ID from response
target.setId(entity.data().getId());

// Persist to local storage
instanceDataManager.writeEntry(target);
```

### Phase 3: ID Persistence

The `writeEntry()` method:

1. Determines appropriate `FileHeader` based on service type
2. Creates `ServiceEntry` with the assigned UUID
3. Writes binary data via `InstanceDataWriter`

```java
public void writeEntry(ServiceTarget target) {
    if (dataFileExists()) {
        return;  // Idempotent - never overwrite
    }

    FileHeader header = getHeader(target.getServiceType());
    ServiceEntry entry = new ServiceEntry(target.getId());

    try (InstanceDataWriter writer = new InstanceDataWriter(dataFile)) {
        writer.writeHeader(header);
        writer.writeEntry(entry);
    }
}
```

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                   Service Startup                           │
└──────────────────────────┬──────────────────────────────────┘
                           ▼
┌─────────────────────────────────────────────────────────────┐
│            InstanceDataManager.dataFileExists()?            │
└──────────────────────────┬──────────────────────────────────┘
                           │
           ┌───────────────┴───────────────┐
           │                               │
           ▼ YES                           ▼ NO
┌─────────────────────┐       ┌─────────────────────────────┐
│ InstanceDataReader  │       │ createDataDirectory()       │
│ → Load existing ID  │       └─────────────┬───────────────┘
│ → Set on target     │                     │
└─────────────────────┘                     ▼
           │               ┌─────────────────────────────────┐
           │               │ HTTP POST /register to router   │
           │               └─────────────┬───────────────────┘
           │                             ▼
           │               ┌─────────────────────────────────┐
           │               │ Router assigns UUID             │
           │               └─────────────┬───────────────────┘
           │                             ▼
           │               ┌─────────────────────────────────┐
           │               │ InstanceDataWriter              │
           │               │ → Write header + entry          │
           │               │ → Persist to .wanaku.dat        │
           │               └─────────────┬───────────────────┘
           │                             │
           └─────────────┬───────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Service registered with known ID               │
│              Heartbeats and state updates use this ID       │
└─────────────────────────────────────────────────────────────┘
```

## Key Classes

| Class                      | Module                   | Responsibility                        |
|----------------------------|--------------------------|---------------------------------------|
| `ZeroDepRegistrationManager` | capabilities-discovery | Orchestrates registration lifecycle   |
| `InstanceDataManager`      | capabilities-data-files  | Coordinates ID persistence            |
| `InstanceDataReader`       | capabilities-data-files  | Reads binary data files               |
| `InstanceDataWriter`       | capabilities-data-files  | Writes binary data files              |
| `FileHeader`               | capabilities-data-files  | Represents file metadata              |
| `ServiceEntry`             | capabilities-data-files  | Contains the service UUID             |

## Design Considerations

### Idempotency
The `writeEntry()` method checks if the file already exists before writing. This prevents accidental overwrites and ensures the original ID persists across the service lifetime.

### Binary Format
Using a fixed-size binary format provides:
- Efficient I/O with predictable buffer sizes
- Fast reads without parsing overhead
- Clear versioning for future format changes

### Thread Safety
- File operations use `FileChannel` for atomic reads/writes
- `InstanceDataReader` and `InstanceDataWriter` implement `AutoCloseable` for proper resource management

## Configuration

The data directory is configured via `RegistrationConfig`:

```java
RegistrationConfig config = new DefaultRegistrationConfig.Builder()
    .withDataDir("/var/wanaku/data")
    .build();
```

If not specified, the implementation uses a reasonable default location.
